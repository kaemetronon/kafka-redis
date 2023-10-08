package kafkaApp.service;

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
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class CsvUtils {

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

    public String updateCsv(String val) {

        File file = new File(csvName);

        try {
            BufferedWriter writer;
            CSVPrinter csvPrinter;
            if (!file.exists()) {
                writer = Files.newBufferedWriter(Paths.get(csvName));
                csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(csvHeaders));
            } else {
                writer = Files.newBufferedWriter(Paths.get(csvName), StandardOpenOption.APPEND
                        , StandardOpenOption.CREATE);
                csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            }
            String[] separated = val.split(",");
            csvPrinter.printRecord(separated);
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "ok";
    }
}
