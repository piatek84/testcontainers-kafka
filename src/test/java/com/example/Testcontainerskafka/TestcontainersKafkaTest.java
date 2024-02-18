package com.example.Testcontainerskafka;

import com.example.Testcontainerskafka.model.LocationNotification;
import com.example.Testcontainerskafka.model.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.TimeUnit;

import static com.example.Testcontainerskafka.Utils.createKafkaConsumer;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpStatus.CREATED;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class TestcontainersKafkaTest {
    @LocalServerPort
    private int port;
    @Value("${api.url}")
    String url;
    @Value("${topic}")
    String topicName;
    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3"));
    @Container
    private static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    static void overridePropertiesToUseTestcontainers(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    public void producerIsSendingExpectedMessage() {
        //given
        KafkaConsumer<String, Notification> consumer = createKafkaConsumer(kafka.getBootstrapServers());
        consumer.subscribe(Collections.singletonList(topicName));

        //when
        given().when().get(url + this.port + "/location").then().statusCode(CREATED.value());

        //then
        Unreliables.retryUntilTrue(30, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, Notification> records = consumer.poll(Duration.ofMillis(100));

            if (records.isEmpty()) {
                return false;
            }

            for (ConsumerRecord<String, Notification> record : records) {
                LocationNotification consumedLocationNotification = (LocationNotification) record.value();
                Assertions.assertNotNull(consumedLocationNotification);
                Assertions.assertNotNull(consumedLocationNotification.getLatitude());
                Assertions.assertNotNull(consumedLocationNotification.getLongitude());
                System.out.println(
                        "https://www.google.com/search?q="
                                + consumedLocationNotification.getLatitude()
                                + ","
                                + consumedLocationNotification.getLongitude());
            }

            return true;
        });

        consumer.unsubscribe();
    }

}
