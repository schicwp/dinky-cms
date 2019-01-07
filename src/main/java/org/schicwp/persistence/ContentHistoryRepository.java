package org.schicwp.persistence;

import org.schicwp.model.ContentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by will.schick on 1/5/19.
 */
interface ContentHistoryRepository extends MongoRepository<ContentHistory,String> {

    Page<ContentHistory> findAllByContentId(String id, Pageable pageable);
}
