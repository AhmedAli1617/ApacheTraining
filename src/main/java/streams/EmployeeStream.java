package streams;

import models.Employee;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.kstream.KStream;
import serdes.EmployeeSerde;

import java.util.Locale;
import java.util.Properties;

public class EmployeeStream {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-string");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EmployeeSerde.class.getName());

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        try {
            KStream<String, Employee> kStream = streamsBuilder.stream("topic3");
            kStream.foreach((k, v) -> System.out.println(" Employee: " + v.getFirstName() + " " + v.getLastName()));

            KStream<String, Employee>[] branches = kStream.branch(
                    (key, value) -> value.getMaritalStatus().equals("single"),
                    (key, value) -> value.getMaritalStatus().equals("married")
            );

            branches[0].to("single-topic");
            branches[1].to("married-topic");

            KStream<String, Employee> upperCaseStream = kStream.mapValues(employee -> {
                employee.setFirstName(employee.getFirstName().toUpperCase(Locale.ROOT));
                employee.setLastName(employee.getLastName().toUpperCase(Locale.ROOT));
                return employee;
            });
            upperCaseStream.to("uppercase-topic");

            Topology topology = streamsBuilder.build();
            KafkaStreams streams = new KafkaStreams(topology, props);
            streams.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                streams.close();
            }));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
