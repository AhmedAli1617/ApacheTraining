package elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Employee;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static constants.ElasticSearchConstants.*;

public class ElasticSearchImpl {

    private static RestHighLevelClient restHighLevelClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        makeConnection();
        System.out.println("Inserting employees...");
        List<Employee> employees = Utils.getEmployeeDummyData();

        for (Employee employee : employees) {
            employee = insertEmployee(employee);
            System.out.println("Employee inserted --> " + employee);
        }

        System.out.println("Getting employee with id '1'...");
        Employee employeeFromDB = getEmployeeById("1");
        System.out.println("Employee from DB  --> " + employeeFromDB);

        System.out.println("Changing name of employee with id '1'...");
        employeeFromDB.setFirstName("Uzair");
        employeeFromDB = updateEmployeeById("1", employeeFromDB);
        System.out.println("Employee updated  --> " + employeeFromDB);

        System.out.println("Deleting employee with id '3'...");
        deleteEmployeeById("3");

        System.out.println("Searching employees using text...");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the search string: ");
            String searchString = sc.nextLine();
            searchEmployee(searchString).forEach((employee1) -> {
                System.out.println(employee1);
            });

        }

    }

    private static synchronized RestHighLevelClient makeConnection() {

        if (restHighLevelClient == null) {
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT_ONE, SCHEME),
                            new HttpHost(HOST, PORT_TWO, SCHEME)));
        }
        return restHighLevelClient;
    }

    private static synchronized void closeConnection() throws IOException {
        restHighLevelClient.close();
        restHighLevelClient = null;
    }

    private static Employee insertEmployee(Employee employee) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", employee.getId());
        dataMap.put("firstName", employee.getFirstName());
        dataMap.put("lastName", employee.getLastName());
        dataMap.put("maritalStatus", employee.getMaritalStatus());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, employee.getId().toString())
                .source(dataMap);
        try {
            restHighLevelClient.index(indexRequest);
        } catch (ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex) {
            ex.getLocalizedMessage();
        }
        return employee;
    }

    private static Employee getEmployeeById(String id) {
        GetRequest getEmployeeRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = restHighLevelClient.get(getEmployeeRequest);
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        }
        return getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), Employee.class) : null;

    }

    private static List<Employee> searchEmployee(String searchValue) {
        try {
            SearchRequest searchRequest = new SearchRequest();
            List<Employee> list = new ArrayList<>();

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(100);
            searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(searchValue).fuzziness(Fuzziness.TWO));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            for (SearchHit hit : searchHits.getHits()) {
                /*list.add(new MetaData((String) sourceAsMap.get("infoHash"),
                        Long.parseLong(String.valueOf(sourceAsMap.get("length"))),
                        (String) sourceAsMap.get("name"), (String) sourceAsMap.get("nameInfo")));*/
                list.add(objectMapper.convertValue(hit.getSourceAsMap(), Employee.class));
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Employee updateEmployeeById(String id, Employee employee) {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);
        try {
            String employeeJson = objectMapper.writeValueAsString(employee);
            updateRequest.doc(employeeJson, XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            return objectMapper.convertValue(updateResponse.getGetResult().getSource(), Employee.class);
        } catch (JsonProcessingException e) {
            e.getMessage();
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        }
        System.out.println("Unable to update employee");
        return null;
    }

    private static void deleteEmployeeById(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        try {
            restHighLevelClient.delete(deleteRequest);
            System.out.println("Employee Deleted");
        } catch (java.io.IOException e) {
            e.getLocalizedMessage();
        }
    }

}
