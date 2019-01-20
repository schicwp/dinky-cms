package org.schicwp.dinky.search;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.content.PermissionService;
import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

}
