package org.schicwp.dinky.config.loader;

import org.schicwp.dinky.config.ContentTypeConfig;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

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


    private List<ContentTypeConfig> contentTypeConfigs = new ArrayList<>();

    @Autowired
    ContentTypeService contentTypeService;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    FieldTypeService fieldTypeService;

    @Value("${dinky.types}")
    private String configDir;

    @PostConstruct
    @Scheduled(fixedRate = 15000L)
    void init(){


        Map<String,ContentType> contentTypes = new HashMap<>();
        List<ContentTypeConfig> contentTypeConfigs = new ArrayList<>();


        Arrays.asList(new File(configDir).listFiles()).forEach( file->{

            try {

                logger.fine("Loading: " + file);
                ContentTypeConfig contentTypeConfig = new Yaml(new Constructor(ContentTypeConfig.class))
                        .load(new FileInputStream(file));


                ContentType contentType = convertContentTypeConfigToContentType(contentTypeConfig);

                contentTypes.put(contentType.getName(), contentType);

                contentTypeConfigs.add(contentTypeConfig);


            }catch (Exception e){
                throw new RuntimeException("Error loading: " + file,e);
            }

        });


        logger.fine("Types: " + contentTypes);
        contentTypeService.setContentTypes(contentTypes);
        setContentTypeConfigs(contentTypeConfigs);




    }

    private ContentType convertContentTypeConfigToContentType(ContentTypeConfig contentTypeConfig) {
        ContentType contentType = new ContentType(
                contentTypeConfig.getName(),
                contentTypeConfig.getFields()
                        .stream()
                        .map(fieldConfig -> new Field(
                                fieldTypeService.getFieldType(fieldConfig.getType()),
                                fieldConfig.isRequired(),
                                fieldConfig.getConfig(),
                                fieldConfig.getName(),
                                fieldConfig.isIndexed()
                        ))
                        .collect(Collectors.toList()),
                contentTypeConfig.getWorkflows(),
                contentTypeConfig.getNameField()
        );

        contentType.getFields().forEach(field -> {
            if (field.isIndexed()){
                logger.fine("Creating index 'content." + field.getName() + "'");
                mongoOperations.indexOps(Content.class).
                        ensureIndex(new Index().on("content." + field.getName(), Sort.Direction.ASC));
            }
        });
        return contentType;
    }

    public synchronized List<ContentTypeConfig> getContentTypeConfigs() {
        return contentTypeConfigs;
    }

    public synchronized void setContentTypeConfigs(List<ContentTypeConfig> contentTypeConfigs) {
        this.contentTypeConfigs = contentTypeConfigs;
    }
}
