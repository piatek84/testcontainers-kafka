package com.example.Testcontainerskafka.controller;

import com.example.Testcontainerskafka.NotificationProducer;
import com.example.Testcontainerskafka.model.LocationNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @GetMapping("/location")
    public ResponseEntity<LocationNotification> location() {
        NotificationProducer producer = new NotificationProducer(bootstrapAddress);
        producer.produce();
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
