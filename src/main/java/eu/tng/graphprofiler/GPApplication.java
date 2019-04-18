package eu.tng.graphprofiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableAsync
@EnableMongoRepositories("eu.tng.repository.dao")
@ComponentScan({"eu.tng"})
public class GPApplication {

    public static void main(String[] args) {
        SpringApplication.run(GPApplication.class, args);
    }

}
