package com.example.demo.service.csv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@Service
@Slf4j
public class CsvUtils {

    private static String csvName;
    // to initialize static field through @Value
    @Value("${csv.name}")
    public void setStaticPath(String name) {
        var sb = new StringBuilder(System.getProperty("user.dir"));
        if (sb.charAt(sb.length() - 1) != '/')
            sb.append("/");
        CsvUtils.csvName = sb.toString().concat(name);
    }

    private static Long csvSize;
    @Value("${csv.size}")
    public void setStaticSize(Long size) {
        CsvUtils.csvSize = size;
    }

    private static String[] csvHeaders;
    @Value("${csv.headers}")
    public void setStaticHeaders(String... headers) {
        CsvUtils.csvHeaders = headers;
    }

    public static void createCsv() {
            log.info("CSV generating starts...");

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvName));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader(csvHeaders))
        ) {
            Random r = new Random();

            for (int i = 0; i < csvSize; i++) {
                csvPrinter.printRecord(i, "text" + i, r.nextInt());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            log.info("Csv creating exception");
            e.printStackTrace();
        }
        log.info("Flushed");
    }

    public static void drop() {
        log.info("Deleting csv...");
        File file = new File(csvName);
        if (file.delete())
            log.info("Success");
        else
            log.error("Failure");
    }

    public static boolean checkIfExists() {
        return new File(csvName).exists();
    }

}
