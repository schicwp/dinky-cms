package org.schicwp.search;

import org.elasticsearch.index.query.QueryBuilders;
import org.schicwp.model.Content;
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

        System.out.println("Finding..." + q);

        Page<Content> content  = searchRepository.search(
                QueryBuilders.queryStringQuery(q),
                PageRequest.of(page, size, Sort.by("modified").descending())
        );

        content.getContent();


        return content;
    }

}
