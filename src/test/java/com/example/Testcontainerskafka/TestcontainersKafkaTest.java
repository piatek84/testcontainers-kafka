package com.example.Testcontainerskafka;

import com.example.Testcontainerskafka.model.LocationNotification;
import com.example.Testcontainerskafka.model.Notification;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.junit.Assert.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class TestcontainersKafkaTest {
    @Container
    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3"));


    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @LocalServerPort
    private int port;

    public static final String URL = "http://localhost:";

    @Test
    public void testNotificationSending() {
        String topicName = "notifications";
        String bootstrapServers = kafka.getBootstrapServers();

//        NotificationProducer producer = new NotificationProducer();
//        producer.setBootstrapServers(bootstrapServers);
//        producer.setTopicName(topicName);

        KafkaConsumer<String, Notification> consumer = getConsumer(bootstrapServers);

        consumer.subscribe(Collections.singletonList(topicName));

//        producer.produce();

        given().when().get(URL + this.port + "/location");

        Unreliables.retryUntilTrue(30, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, Notification> records = consumer.poll(Duration.ofMillis(100));

            if (records.isEmpty()) {
                return false;
            }

            for (ConsumerRecord<String, Notification> record : records) {
                LocationNotification consumedLocationNotification = (LocationNotification) record.value();
                assertNotNull(consumedLocationNotification);
                assertNotNull(consumedLocationNotification.getLatitude());
                assertNotNull(consumedLocationNotification.getLongitude());
                System.out.println("https://www.google.com/search?q="+consumedLocationNotification.getLatitude() + "," + consumedLocationNotification.getLongitude());
            }

            return true;
        });

        consumer.unsubscribe();
    }

    @NotNull
    private static KafkaConsumer<String, Notification> getConsumer(String bootstrapServers) {
        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.setProperty(GROUP_ID_CONFIG, "test");
        props.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.setProperty(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, "com.example.Testcontainerskafka.deserializer.NotificationDeserializer");
        return new KafkaConsumer<>(props);
    }

}
