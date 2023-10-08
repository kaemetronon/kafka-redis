package com.example.demo.model.dto.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TokenResponse {
    private final String jwt;
}
