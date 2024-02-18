package com.example.Testcontainerskafka;

import com.example.Testcontainerskafka.model.Notification;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class Utils {

    @NotNull
    static KafkaConsumer<String, Notification> createKafkaConsumer(String bootstrapServers) {
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
