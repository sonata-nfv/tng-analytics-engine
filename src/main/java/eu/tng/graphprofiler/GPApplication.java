package eu.tng.graphprofiler;

import eu.tng.repository.dao.AnalyticServiceRepository;
import eu.tng.repository.domain.AnalyticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("eu.tng.repository.dao")
public class GPApplication {

    public static void main(String[] args) {
        SpringApplication.run(GPApplication.class, args);
    }

}
