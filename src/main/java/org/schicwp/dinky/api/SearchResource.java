package org.schicwp.dinky.api;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentHistory;
import org.schicwp.dinky.persistence.ContentHistoryRepository;
import org.schicwp.dinky.content.ContentService;
import org.schicwp.dinky.search.SearchRepository;
import org.schicwp.dinky.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by will.schick on 1/6/19.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchResource {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;


    @Autowired
    ContentService contentService;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Autowired
    AuthService authService;

    @Autowired
    SearchService searchService;

    @GetMapping("{index}")
    public Page<Content> search(
            @PathVariable("index") String index,
            @RequestParam("q") String q,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page
    ){

        return searchService.withIndex(index,searchRepository -> {
            return searchRepository.search(
                    QueryBuilders.queryStringQuery(q),
                    PageRequest.of(page,size, Sort.by("modified").descending())
            );
        });
    }

    @DeleteMapping("{index}")
    public void rebuildIndex(@PathVariable("index") String index){

        authService.withSystemUser(()->{

            elasticsearchTemplate.deleteIndex(index);

            searchService.withIndex(index,searchRepository -> {
                Page<Content> content;

                int i = 0;
                do {
                    content = contentService.find(
                            Query.query(Criteria.where("searchVersion").ne(null)),
                            PageRequest.of(i++, 100)
                    );

                    content.forEach(c->{
                        ContentHistory contentHistory = contentHistoryRepository.findByContentIdAndContentVersion(
                                c.getId(),c.getSearchVersions().get(index)
                        );

                        searchRepository.save(contentHistory.getContent());
                    });

                }while (!content.isLast());
            });

        });

    }

    @GetMapping
    public Collection<String> getIndexes(){
        return searchService.getIndexes();
    }


}
