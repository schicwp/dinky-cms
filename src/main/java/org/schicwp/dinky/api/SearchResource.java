package org.schicwp.dinky.api;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentHistory;
import org.schicwp.dinky.persistence.ContentHistoryRepository;
import org.schicwp.dinky.content.ContentService;
import org.schicwp.dinky.search.SearchContent;
import org.schicwp.dinky.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/6/19.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchResource {

    private static final Logger logger = Logger.getLogger(SearchResource.class.getCanonicalName());

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
    public Page<SearchContent> search(
            @PathVariable("index") String index,
            @RequestParam("q") String q,
            @PageableDefault(sort = "modified", direction = Sort.Direction.DESC) Pageable pageable
    ){

        return searchService.search(index,QueryBuilders.queryStringQuery(q),pageable );

    }

    @DeleteMapping("{index}")
    public void rebuildIndex(@PathVariable("index") String index){

        authService.withSystemUser(()->{

            logger.info("Rebuilding index: [" + index + "]");
            elasticsearchTemplate.deleteIndex(index);


            Page<Content> content;

            long count = 0;
            int i = 0;
            do {
                content = contentService.find(
                        Query.query(Criteria.where("searchVersions." + index).ne(null)),
                        PageRequest.of(i++, 100)
                );

                logger.info("Re-indexing [" + content.getContent().size() +"] items");

                count += content.getContent().size();

                content.forEach(c->{
                    ContentHistory contentHistory = contentHistoryRepository.findByContentIdAndContentVersion(
                            c.getId(),c.getSearchVersions().get(index)
                    );

                    searchService.addToIndex(index,new SearchContent(contentHistory.getContent()));
                });

            }while (!content.isLast());


            logger.info("Re-indexed [" + count +"] total items");


        });

    }

    @GetMapping
    public Collection<String> getIndexes(){
        return searchService.getIndexes();
    }


}
