package deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Employee;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class EmployeeDeserializer implements Deserializer {
    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Employee deserialize(String arg1, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        Employee employee = null;
        try {
            employee = mapper.readValue(bytes, Employee.class);
        } catch (Exception e) {

        }
        return employee;
    }

    @Override
    public void close() {

    }
}
