package org.schicwp.api;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.model.Content;
import org.schicwp.search.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by will.schick on 1/6/19.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchResource {


    @Autowired
    SearchRepository searchRepository;

    @GetMapping
    public Page<Content> search(@RequestParam("q") String q){

        Pageable pagerequest = PageRequest.of(0,10);

        return searchRepository.search(QueryBuilders.queryStringQuery(q), pagerequest);
    }


}
