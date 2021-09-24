package streams;

import models.Employee;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import serializers.EmployeeSerializer;
import utils.Utils;

import java.util.List;
import java.util.Properties;

import static constants.KakfaConstants.*;

public class ProducerForStream {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS, "localhost:9092");
        properties.put(KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER, EmployeeSerializer.class);

        List<Employee> employees = Utils.getEmployeeDummyData();

        Producer kafkaProducer = new KafkaProducer(properties);
        try {
            for(Employee employeeObj: employees) {
                ProducerRecord<String, Employee> message = new ProducerRecord("topic3", "employee_info", employeeObj);
                kafkaProducer.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.close();
        }
    }
}
