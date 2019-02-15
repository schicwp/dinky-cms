package org.schicwp.dinky.search;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by will.schick on 1/15/19.
 */
@Service
public class SearchService {


    public static final String ES_TYPENAME = "contentType";

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    //@Autowired
    //ResultsMapper entityMapper;

    @Autowired
    ElasticsearchConverter elasticsearchConverter;



    public String addToIndex(String index, SearchContent content){

        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(content.getId());
        indexQuery.setIndexName(index);
        indexQuery.setType(ES_TYPENAME);
        indexQuery.setObject(content);

        return elasticsearchTemplate.index(indexQuery);
    }

    public String deleteFromIndex(String index, String id){
        return elasticsearchTemplate.delete(index,ES_TYPENAME,id);
    }

    public Page<SearchContent> search(String index, QueryBuilder queryBuilder, Pageable pageable){

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withPageable(pageable)
                .withIndices(index)
                .withTypes(ES_TYPENAME)
                .build();


        return elasticsearchTemplate.query(searchQuery, response -> {

            ResultsMapper resultsMapper = new DefaultResultMapper(elasticsearchConverter.getMappingContext());

            Page<SearchContent> searchContents = resultsMapper.mapResults(response,SearchContent.class,pageable);

            //add in the search score
            SearchHit[] hits = response.getHits().getHits();

            for (SearchHit hit : hits) {
                for (SearchContent content: searchContents.getContent()){
                    if (hit.getId().equals(content.getId()))
                        content.setScore(hit.getScore());
                }
            }
            return searchContents;
        });

    }


    public Collection<String> getIndexes(){
        Collection<String> result = new ArrayList<>();

        GetSettingsResponse response = elasticsearchTemplate.getClient().admin().indices().prepareGetSettings().get();

        for (ObjectObjectCursor<String, Settings> cursor : response.getIndexToSettings()) {
            String index = cursor.key;
            result.add(index);
        }

        return result;
    }

}
