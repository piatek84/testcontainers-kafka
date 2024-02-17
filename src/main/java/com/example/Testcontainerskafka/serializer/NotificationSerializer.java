package com.example.Testcontainerskafka.serializer;

import com.example.Testcontainerskafka.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class NotificationSerializer implements Serializer<Notification> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationSerializer() {
        //Nothing to do
    }

    @Override
    public void configure(Map<String, ?> config, boolean isKey) {
        //Nothing to Configure
    }

    @Override
    public byte[] serialize(String topic, Notification data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }

    @Override
    public void close() {

    }
}