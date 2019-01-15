package org.schicwp.persistence;

import org.schicwp.auth.AuthService;
import org.schicwp.auth.User;
import org.schicwp.model.Content;
import org.schicwp.model.ContentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class ContentService {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    AuthService authService;

    @Autowired
    QueryTotalCache queryTotalCache;

    public Content save(Content content) {

        queryTotalCache.invalidate();

        contentHistoryRepository.save(new ContentHistory(content));

        return contentRepository.save(content);
    }

    public Page<Content> getHistory(String id, Pageable pageable){
        return contentHistoryRepository
                .findAllByContentId(id,pageable)
                .map(ContentHistory::getContent);
    }


    public Optional<Content> findById(String s) {
        return contentRepository.findById(s);
    }

    public Page<Content> find(Query query,Pageable pageable) {

        User user = authService.getCurrentUser();



        List<Criteria> criteria = user.getGroups().stream().map( s->
                        Criteria.where("permissions.group." + s + ".read").is(true)
        ).collect(Collectors.toList());

        criteria.add(
                Criteria.where("owner").is(user.getUsername()).and("permissions.owner.read").is(true)
        );

        criteria.add(
                Criteria.where("permissions.other.read").is(true)
        );



        Criteria userCrit =new Criteria()
                .orOperator(
                        criteria.toArray(new Criteria[0])
                );


        Query finalQuery = query.addCriteria(userCrit).with(pageable);

        List<Content> result =  mongoTemplate.find(finalQuery,Content.class);

        return  PageableExecutionUtils.getPage(
                result,
                pageable,
                () -> queryTotalCache.getCountForQuery(finalQuery)
        );
    }
}
