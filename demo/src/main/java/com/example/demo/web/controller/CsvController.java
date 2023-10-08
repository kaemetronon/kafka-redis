package com.example.demo.web.controller;

import com.example.demo.service.csv.ICsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/csv")
public class CsvController {

    private final ICsvService csvService;

    @GetMapping("/create")
    public ResponseEntity<?> createCsv() {
        csvService.createCsv();
        return ResponseEntity.ok().body("Created");
    }

    @GetMapping("/pass")
    public ResponseEntity<?> passToConsumer() {
        csvService.passToKafka();
        return ResponseEntity.ok().body("Passed to kafka");
    }

    @DeleteMapping("/drop")
    public ResponseEntity<?> dropCsv() {
      csvService.dropCsv();
        return ResponseEntity.ok().body("Deleted");
    }
}
