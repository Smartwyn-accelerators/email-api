package com.fastcode.emailApi.application.dto;

public class EmailEventPayload {
    private String trackingId;
    private String eventType; // "open" or "click"
    private String timestamp;

    public EmailEventPayload(String trackingId, String eventType, String timestamp) {
        this.trackingId = trackingId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and setters
}
