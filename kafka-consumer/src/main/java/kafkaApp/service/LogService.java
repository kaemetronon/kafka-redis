package kafkaApp.service;

import kafkaApp.model.LogEntity;
import kafkaApp.repo.LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogsRepository logsRepository;
    private final CsvUtils csvUtils;

    @KafkaListener(id = "requestConsumer", topics = "${kafka.topic.request}", containerFactory = "singleFactory")
    public void listenRequest(String event) {
        event = event.replace("\"", "");
        System.out.println(event);
        logsRepository.save(new LogEntity(event));
    }

    @KafkaListener(id = "csvConsumer", topics = "${kafka.topic.csv}", containerFactory = "singleFactory")
    public void listenCsv(String event) {
        event = event.replace("\"", "");
        csvUtils.updateCsv(event);
    }
}
