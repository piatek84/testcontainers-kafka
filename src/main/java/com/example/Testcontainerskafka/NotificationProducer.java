package com.example.Testcontainerskafka;

import com.example.Testcontainerskafka.model.LocationNotification;
import com.example.Testcontainerskafka.serializer.NotificationSerializer;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
public class NotificationProducer {


    private String bootstrapServers;
    private String topicName;

    public NotificationProducer(String bootstrapServers, String topicName) {
        this.bootstrapServers = bootstrapServers;
        this.topicName = topicName;
    }

    public void produce(){

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NotificationSerializer.class.getName());

        Faker faker = new Faker();

        LocationNotification locationNotification = new LocationNotification();
        locationNotification.setLongitude(faker.address().longitude());
        locationNotification.setLatitude(faker.address().latitude());

        KafkaProducer<String, LocationNotification> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, LocationNotification> producerRecord = new ProducerRecord<>(topicName, locationNotification);

        System.out.println("************ Producing ************ " + producerRecord.value());
        producer.send(producerRecord);

        producer.flush();
        producer.close();
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}