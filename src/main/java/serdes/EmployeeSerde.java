package serdes;

import deserializers.EmployeeDeserializer;
import models.Employee;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import serializers.EmployeeSerializer;

public class EmployeeSerde implements Serde<Employee> {
    private EmployeeSerializer employeeSerializer = new EmployeeSerializer();
    private EmployeeDeserializer employeeDeserializer = new EmployeeDeserializer();
    @Override
    public Serializer<Employee> serializer() {
        return employeeSerializer;
    }

    @Override
    public Deserializer<Employee> deserializer() {
        return employeeDeserializer;
    }
}
