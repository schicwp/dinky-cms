package org.schicwp.dinky.api;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.search.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    SearchRepository searchRepository;

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


}
