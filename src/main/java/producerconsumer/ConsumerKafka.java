package producerconsumer;

import deserializers.EmployeeDeserializer;
import models.Employee;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Properties;

import static constants.KakfaConstants.*;

public class ConsumerKafka {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put(KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(VALUE_DESERIALIZER, EmployeeDeserializer.class);
        properties.put(GROUP_ID, "test-group");

        Consumer<String, Employee> consumer = new KafkaConsumer<>( properties );
        consumer.subscribe( Collections.singletonList( "test-topic" ) );

        while( true ){
            ConsumerRecords<String, Employee> records = consumer.poll( 100 );
            for (ConsumerRecord<String, Employee> message : records) {
                if(message!=null) {
                    System.out.println("Message received " + message.value().toString());
                }
            }
        }

    }
}
