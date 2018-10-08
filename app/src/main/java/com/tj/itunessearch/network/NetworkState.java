package com.tj.itunessearch.network;


public class NetworkState {

    public enum Status{
        STATE_RUNNING,
        STATE_SUCCESS,
        STATE_FAILED
    }


    private final Status status;
    private final String message;

    public static final NetworkState COMPLETED;
    public static final NetworkState LOADING;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.message = msg;
    }

    static {
        COMPLETED = new NetworkState(Status.STATE_SUCCESS,"Success");
        LOADING = new NetworkState(Status.STATE_RUNNING,"Running");
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
