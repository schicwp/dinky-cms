package org.schicwp.dinky;

import org.schicwp.dinky.api.ContentResource;
import org.schicwp.dinky.api.dto.ContentSubmission;
import org.schicwp.dinky.security.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Collections;

/**
 * Created by will.schick on 1/5/19.
 */
@SpringBootApplication
@Import({ProviderConfig.class})
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
