package producerconsumer;

import models.Employee;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import serializers.EmployeeSerializer;
import java.util.Properties;

import static constants.KakfaConstants.*;

public class ProducerKafka {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS, "localhost:9092");
        properties.put(KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER, EmployeeSerializer.class);

        Employee employee = new Employee();
        employee.setId(new Long(1L));
        employee.setFirstName("Ahmed");
        employee.setLastName("Ali");
        employee.setMaritalStatus("single");

        Producer kafkaProducer = new KafkaProducer(properties);
        try {
            ProducerRecord<String, Employee> message = new ProducerRecord("test-topic", "employee_info", employee);
            kafkaProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.close();
        }
    }
}
