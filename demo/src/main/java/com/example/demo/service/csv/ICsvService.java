package com.example.demo.service.csv;

public interface ICsvService {

    void createCsv();

    void passToKafka();

    void dropCsv();
}
