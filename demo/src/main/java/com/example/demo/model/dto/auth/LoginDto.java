package com.example.demo.model.dto.auth;

import lombok.Data;

@Data
public class LoginDto {
    private final String username;
    private final String password;
}
