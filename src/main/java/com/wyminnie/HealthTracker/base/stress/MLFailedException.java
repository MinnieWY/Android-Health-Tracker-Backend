package com.wyminnie.healthtracker.base.stress;

public class MLFailedException extends Exception {

    public MLFailedException() {
        super();
    }

    public MLFailedException(String message) {
        super(message);
    }

    public MLFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MLFailedException(Throwable cause) {
        super(cause);
    }
}