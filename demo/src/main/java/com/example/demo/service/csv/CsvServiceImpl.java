package com.example.demo.service.csv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvServiceImpl implements ICsvService {

    private final KafkaTemplate<String, String> kafka;

    @Value("${kafka.topic.csv}")
    private String kafkaTopic;

    @Value("${csv.headers}")
    private String[] csvHeaders;

    private String csvName;
    @Value("${csv.name}")
    public void setStaticPath(String name) {
        var sb = new StringBuilder(System.getProperty("user.dir"));
        if (sb.charAt(sb.length() - 1) != '/')
            sb.append("/");
        csvName = sb.toString().concat(name);
    }

    @Override
    public void createCsv() {
        CsvUtils.createCsv();
    }

    @Override
    public void passToKafka() {
        if (!CsvUtils.checkIfExists()) {
            log.info("File didn't exists before. creating..");
            CsvUtils.createCsv();
        }

        CSVParser parser = null;

        try {
            URL url = new File(csvName).toURI().toURL();
            Reader reader = new InputStreamReader(new BOMInputStream(url.openStream()), StandardCharsets.UTF_8);
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        } catch (MalformedURLException e) {
            log.error("Url exception");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("IO exception");
            e.printStackTrace();
        }

//        send headers
        StringBuilder sb = new StringBuilder();
        for (String header : csvHeaders
        ) {
            sb.append(header);
            sb.append(',');
        }
        sb.setLength(sb.length() - 1);
        kafka.send(kafkaTopic, sb.toString());
        sb.setLength(0);

//        send values
        for (final CSVRecord record : parser) {

            for (String header : csvHeaders
            ) {
                var val = record.get(header);
                sb.append(val);
                sb.append(',');
            }
            sb.setLength(sb.length() - 1);
            kafka.send(kafkaTopic, sb.toString());
            sb.setLength(0);
        }

        log.info("Sent into kafka topic: {}", kafkaTopic);
    }

    @Override
    public void dropCsv() {
        CsvUtils.drop();
    }
}
