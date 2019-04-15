package eu.tng.graphprofiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableMongoRepositories("eu.tng.repository.dao")
public class GPApplication {

    public static void main(String[] args) {
        SpringApplication.run(GPApplication.class, args);
    }

}
