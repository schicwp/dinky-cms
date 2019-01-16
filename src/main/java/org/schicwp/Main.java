package org.schicwp;

import org.schicwp.api.ContentResource;
import org.schicwp.api.dto.ContentSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.Collections;

/**
 * Created by will.schick on 1/5/19.
 */
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = MongoRepositoriesAutoConfiguration.class)
@EnableElasticsearchRepositories(basePackages = "org/schicwp/search")
@EnableMongoRepositories(basePackages = "org/schicwp/persistence")
//@EnableElasticsearchRepositories(basePackages = "org/schicwp/search")
public class Main {

    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    ContentResource contentResource;

    @Bean
    FileTemplateResolver templateResolver(){
        FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setPrefix("./web/");
        fileTemplateResolver.setSuffix(".html");
        return fileTemplateResolver;
    }

    /*@Bean
    public Client client() throws Exception {
        Settings elasticsearchSettings = Settings.builder()
                .put("client.transport.sniff", true)
                //.put("path.home", elasticsearchHome)
                //.put("cluster.name", clusterName)
                .build();
        TransportClient client = new PreBuiltTransportClient(elasticsearchSettings);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        return client;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception{
        return new ElasticsearchTemplate(client());
    }
    */

    @Bean
    CommandLineRunner commandLineRunner(){
        return (args )->{



           // for (int i = 0; i < 1_000_000; i++)
            int i = 1;
            {



                ContentSubmission contentSubmission = new ContentSubmission();
                contentSubmission.setAction("Publish");
                contentSubmission.setType("Concept");
                contentSubmission.getWorkflow().put("AssignToGroup", Collections.singletonMap("group", "SysAdmins"));
                contentSubmission.getWorkflow().put("AssignToUser", Collections.singletonMap("user", "will.schick"));

                contentSubmission.getContent().put("title", "Item: " + i);
                contentSubmission.getContent().put("code", "codely");
                contentSubmission.getContent().put("number", i);
                contentSubmission.getContent().put("description", "ASDASD" + System.currentTimeMillis());

                long start = System.currentTimeMillis();

                contentResource.postContent(contentSubmission);

                long stop = System.currentTimeMillis();

                if (i%1000 == 0)
                    System.out.println((stop-start));
            }



        };
    }
}
