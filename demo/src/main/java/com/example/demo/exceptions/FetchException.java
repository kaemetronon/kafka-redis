package com.example.demo.exceptions;

public class FetchException extends RuntimeException {
    public FetchException(String err) {
        super(err);
    }
}