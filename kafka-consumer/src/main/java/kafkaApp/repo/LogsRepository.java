package kafkaApp.repo;

import kafkaApp.model.LogEntity;
import org.springframework.data.repository.CrudRepository;

public interface LogsRepository extends CrudRepository<LogEntity, Integer> {
}
