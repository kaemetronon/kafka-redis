package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@RequiredArgsConstructor
@ActiveProfiles("test")
@SpringBootTest
abstract class AbstractTest {
}
