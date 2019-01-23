package org.schicwp.dinky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by will.schick on 1/5/19.
 */
@SpringBootApplication
@EnableScheduling
@EnableWebSecurity
@EnableAutoConfiguration(exclude = MongoRepositoriesAutoConfiguration.class)
@EnableElasticsearchRepositories(basePackages = "org/schicwp/dinky/search")
@EnableMongoRepositories(basePackages = "org/schicwp/dinky/persistence")
public class Main {

    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

}
