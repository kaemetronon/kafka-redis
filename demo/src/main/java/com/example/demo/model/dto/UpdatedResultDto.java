package com.example.demo.model.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdatedResultDto {
    private final Integer totalCount;
    private final Timestamp lastSaved;
}