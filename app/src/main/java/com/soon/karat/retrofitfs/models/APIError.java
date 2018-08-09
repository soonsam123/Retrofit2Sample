package com.soon.karat.retrofitfs.models;

public class APIError {
    private int statusCode;
    private String endpoint;
    private String message = "Unknown error";

    public int getStatusCode() {
        return statusCode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getMessage() {
        return message;
    }
}
