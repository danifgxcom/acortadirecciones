package com.danifgx.acortadirecciones.config;

import com.danifgx.acortadirecciones.entity.Url;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;


@Component
public class MongoConfig {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoConfig(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @PostConstruct
    public void initIndexesAfterStartup() {
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

        resolver.resolveIndexFor(Url.class).forEach(indexDefinitionHolder -> {
            if (indexDefinitionHolder.getIndexOptions().containsKey("expireAfterSeconds")) {
                mongoTemplate.indexOps(Url.class).ensureIndex(indexDefinitionHolder);
            }
        });
    }
}
