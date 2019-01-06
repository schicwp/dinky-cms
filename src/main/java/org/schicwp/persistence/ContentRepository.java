package org.schicwp.persistence;

import org.schicwp.model.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by will.schick on 1/4/19.
 */
interface ContentRepository extends MongoRepository<Content,String> {


}
