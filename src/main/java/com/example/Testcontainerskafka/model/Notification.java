package com.example.Testcontainerskafka.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

public abstract class Notification implements Serializable {

    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        timestamp = Timestamp.from(Instant.now());
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timestamp", getTimestamp()).toString();
    }
}
