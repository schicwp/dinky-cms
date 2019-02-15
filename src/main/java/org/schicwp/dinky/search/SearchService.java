package org.schicwp.dinky.search;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.content.PermissionService;
import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by will.schick on 1/15/19.
 */
@Service
public class SearchService {


    @Autowired
    SearchRepository searchRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    private final ThreadLocal<String> currentIndex = new ThreadLocal<>();



    public <T> T withIndex(String index, Function<SearchRepository,T> query){
        currentIndex.set(index.toLowerCase());
        try {
            return query.apply(searchRepository);
        } finally {
            currentIndex.remove();
        }
    }

    public  void withIndex(String index, Consumer<SearchRepository> query){
        currentIndex.set(index.toLowerCase());
        try {
            query.accept(searchRepository);
        } finally {
            currentIndex.remove();
        }
    }



    public String currentIndex(){

        String name = currentIndex.get();

        if (name == null)
            name = "default";

        return name;
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
