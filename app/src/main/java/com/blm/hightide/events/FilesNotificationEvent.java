package com.blm.hightide.events;

public class FilesNotificationEvent {

    private String message;

    private int increment;

    public FilesNotificationEvent(String message, int increment) {
        this.message = message;
        this.increment = increment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }
}
