package org.schicwp.persistence;

import org.schicwp.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by will.schick on 1/6/19.
 *
 * TODO - I hate this
 *
 * this is here because I guess arbitrary counts are slow in mongo? //oh well...
 */
@Service
public class QueryTotalCache {


    @Autowired
    MongoTemplate mongoTemplate;

    ConcurrentHashMap<String,Long> cachedTotals = new ConcurrentHashMap<>();


    public void invalidate(){
        this.cachedTotals.clear();
    }

    public long getCountForQuery(Query finalQuery) {
        String queryString = finalQuery.toString();

        if (!cachedTotals.containsKey(queryString)) {
            cachedTotals.put(queryString, mongoTemplate.count(finalQuery, Content.class));
        }

        return cachedTotals.get(queryString);
    }

}
