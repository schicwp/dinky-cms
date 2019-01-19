package org.schicwp.dinky.api;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentHistory;
import org.schicwp.dinky.persistence.ContentHistoryRepository;
import org.schicwp.dinky.content.ContentService;
import org.schicwp.dinky.search.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

/**
 * Created by will.schick on 1/6/19.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchResource {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    ContentService contentService;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Autowired
    AuthService authService;

    @GetMapping
    public Page<Content> search(
            @RequestParam("q") String q,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page
    ){

        return searchRepository.search(
                QueryBuilders.queryStringQuery(q),
                PageRequest.of(page,size, Sort.by("modified").descending())
        );
    }

    @DeleteMapping
    public void rebuildIndex(){

        authService.withSystemUser(()->{

            elasticsearchTemplate.deleteIndex(Content.class);

            Page<Content> content;

            int i = 0;

            do {
                content = contentService.find(
                        Query.query(Criteria.where("searchVersion").ne(null)),
                        PageRequest.of(i++, 100)
                );

                content.forEach(c->{
                    ContentHistory contentHistory = contentHistoryRepository.findByContentIdAndContentVersion(
                            c.getId(),c.getVersion()
                    );

                    searchRepository.save(contentHistory.getContent());
                });

            }while (!content.isLast());

        });

    }


}
