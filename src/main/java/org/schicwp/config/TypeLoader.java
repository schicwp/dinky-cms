package org.schicwp.config;

import org.schicwp.model.Content;
import org.schicwp.model.type.ContentType;
import org.schicwp.model.type.ContentTypeService;
import org.schicwp.model.type.FieldFactory;
import org.schicwp.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/5/19.
 */
@Component
public class TypeLoader {

    private static final Logger logger = Logger.getLogger(TypeLoader.class.getCanonicalName());

    @Autowired
    ContentTypeService contentTypeService;

    @Autowired
    FieldFactory fieldFactory;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    MongoOperations mongoOperations;

    String configDir = "./types";

    @PostConstruct
    @Scheduled(fixedRate = 15000L)
    void init(){


        Map<String,ContentType> contentTypes = new HashMap<>();


        Arrays.asList(new File(configDir).listFiles()).forEach( file->{

            try {

                logger.fine("Loading: " + file);
                Map<String, Object> obj = new Yaml().load(new FileInputStream(file));



                List<Map<String,Object>> fieldConfigs = (List)obj.get("fields");

                String workflow = (String)obj.get("workflow");
                String name = (String)obj.get("name");

                ContentType contentType = new ContentType(
                        name, fieldConfigs.stream().map(fieldFactory::createField).collect(Collectors.toList()),
                        workflowService.getWorkflow(workflow),
                        (String)obj.get("nameField")
                );

                contentType.getFields().forEach(field -> {
                    if (field.isIndexed()){
                        logger.fine("Creating index 'content." + field.getName() + "'");
                        mongoOperations.indexOps(Content.class).
                                ensureIndex(new Index().on("content." + field.getName(), Sort.Direction.ASC));
                    }
                });


                contentTypes.put(name, contentType);


            }catch (Exception e){
                throw new RuntimeException("Error loading: " + file,e);
            }

        });


        logger.fine("Types: " + contentTypes);
        contentTypeService.setContentTypes(contentTypes);




    }




}
