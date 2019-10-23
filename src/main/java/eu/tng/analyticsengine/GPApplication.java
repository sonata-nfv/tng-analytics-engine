package eu.tng.analyticsengine;

import eu.tng.analyticsengine.Messaging.LogsFormat;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableMongoRepositories("eu.tng.repository.dao")
public class GPApplication {

    public static void main(String[] args) {
        SpringApplication.run(GPApplication.class, args);

    }

    @Bean
    public LogsFormat logsFormat() {
        return new LogsFormat();
    }

    @Bean
    public DefaultServices defaultServices() {
        return new DefaultServices();
    }

    @PostConstruct
    public void init() {
        defaultServices().getAnalyticServiceList();
    }
}
