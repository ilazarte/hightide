package com.blm.hightide.events;

public class FilesNotificationEvent {

    private String message;

    private int increment;

    private int max;

    public FilesNotificationEvent(String message, int increment, int max) {
        this.message = message;
        this.increment = increment;
        this.max = max;
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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
