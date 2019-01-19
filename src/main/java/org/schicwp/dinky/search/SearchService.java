package org.schicwp.dinky.search;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.dinky.content.PermissionService;
import org.schicwp.dinky.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by will.schick on 1/15/19.
 */
@Service
public class SearchService {

    @Autowired
    SearchRepository searchRepository;

    public Page<Content> find(String q, int page, int size) {

        return searchRepository.search(
                QueryBuilders.queryStringQuery(q),
                PageRequest.of(page, size, Sort.by("modified").descending())
        );
    }

}
