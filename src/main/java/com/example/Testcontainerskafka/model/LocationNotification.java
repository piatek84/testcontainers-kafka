package com.example.Testcontainerskafka.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class LocationNotification extends Notification implements Serializable {

    private String longitude;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timestamp", super.getTimestamp()).append("latitude", latitude).append("longitude", longitude).toString();
    }
}
