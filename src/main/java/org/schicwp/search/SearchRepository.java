package org.schicwp.search;

import org.schicwp.model.Content;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by will.schick on 1/6/19.
 */
@Repository
public interface SearchRepository extends ElasticsearchRepository<Content,String> {
}
