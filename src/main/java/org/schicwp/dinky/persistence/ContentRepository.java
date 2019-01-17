package org.schicwp.dinky.persistence;

import org.schicwp.dinky.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by will.schick on 1/4/19.
 */
interface ContentRepository extends MongoRepository<Content,String> {


}
