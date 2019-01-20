package org.schicwp.dinky.persistence;

import org.schicwp.dinky.model.ContentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by will.schick on 1/5/19.
 */
public interface ContentHistoryRepository extends MongoRepository<ContentHistory,String> {

    Page<ContentHistory> findAllByContentIdOrderByContentVersionDesc(String id, Pageable pageable);

    ContentHistory findByContentIdAndContentVersion(String id, int version);
}
