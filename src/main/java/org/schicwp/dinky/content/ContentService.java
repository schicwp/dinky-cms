package org.schicwp.dinky.content;

import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.schicwp.dinky.exceptions.PermissionException;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentHistory;
import org.schicwp.dinky.persistence.ContentHistoryRepository;
import org.schicwp.dinky.persistence.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/5/19.
 */
@Service
public class ContentService {

    private static final Logger logger = Logger.getLogger(ContentService.class.getCanonicalName());

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PermissionService permissionService;

    @Autowired
    QueryTotalCache queryTotalCache;

    @Autowired
    AuthService authService;

    @Transactional
    public Content save(Content content) {

        queryTotalCache.invalidate();

        contentHistoryRepository.save(new ContentHistory(content));

        return contentRepository.save(content);
    }

    public Page<Content> getHistory(String id, Pageable pageable){

        if (!this.findById(id).isPresent())
            throw new NoSuchElementException("No such element");

        return contentHistoryRepository
                .findAllByContentIdOrderByContentVersionDesc(id,pageable)
                .map(ContentHistory::getContent);
    }

    public Content getHistoricalVersion(String id, int version){

        if (!this.findById(id).isPresent())
            throw new NoSuchElementException("No such element");

        return contentHistoryRepository
                .findByContentIdAndContentVersion(id,version)
                .getContent();
    }

    @Transactional
    public Content revertToHistoricalVersion(String id, int version){

        if (!this.findById(id).isPresent())
            throw new NoSuchElementException("No such element");

        Content oldVersion =  contentHistoryRepository
                .findByContentIdAndContentVersion(id,version)
                .getContent();

        Content currentVersion = contentRepository.findById(id).orElseThrow(NoSuchElementException::new);

        if (!permissionService.allowWrite(currentVersion))
            throw new PermissionException();

        logger.info(String.format("Reverting %s to %s",currentVersion,oldVersion));

        oldVersion.setVersion(currentVersion.getVersion() + 1);

        return save(oldVersion);
    }


    public Optional<Content> findById(String s) {
        Optional<Content> byId = contentRepository
                .findById(s);

        if (byId.isPresent() && !permissionService.allowRead(byId.get()))
            throw new PermissionException();

        return byId;
    }

    public Page<Content> find(Query query,Pageable pageable) {

        Query finalQuery = query.addCriteria(
                permissionService.getPermissionFilter()
        ).with(pageable);


        return  PageableExecutionUtils.getPage(
                mongoTemplate.find(finalQuery,Content.class),
                pageable,
                () -> queryTotalCache.getCountForQuery(finalQuery)
        );
    }

    public Page<Content> findAssigned(Query query,Pageable pageable) {

        Query finalQuery = query.addCriteria(
                getAssignedPermissionFilter()
        ).with(pageable);


        return  PageableExecutionUtils.getPage(
                mongoTemplate.find(finalQuery,Content.class),
                pageable,
                () -> queryTotalCache.getCountForQuery(finalQuery)
        );
    }

    public Page<Content> findMine(Query query,Pageable pageable) {

        Query finalQuery = query.addCriteria(
                getOwnedPermissionFilter()
        ).with(pageable);


        return  PageableExecutionUtils.getPage(
                mongoTemplate.find(finalQuery,Content.class),
                pageable,
                () -> queryTotalCache.getCountForQuery(finalQuery)
        );
    }

    public long count(Query query){
        return queryTotalCache
                .getCountForQuery(
                        query.addCriteria(
                                permissionService.getPermissionFilter()
                        )
                );
    }

    public long countAssigned(Query query){
        return queryTotalCache
                .getCountForQuery(
                        query.addCriteria(
                                getAssignedPermissionFilter()
                        )
                );
    }

    public long countOwned(Query query){
        return queryTotalCache
                .getCountForQuery(
                        query.addCriteria(
                                getOwnedPermissionFilter()
                        )
                );
    }

    private Criteria getAssignedPermissionFilter() {

        User user = authService.getCurrentUser();

        return new Criteria().andOperator(
                permissionService.getPermissionFilter(),
                new Criteria().orOperator(
                        Criteria.where("assignedUser").is(user.getUsername()),
                        Criteria.where("assignedGroup").in(user.getGroups())
                )
        );

    }

    private Criteria getOwnedPermissionFilter() {

        User user = authService.getCurrentUser();

        return new Criteria().andOperator(
                permissionService.getPermissionFilter(),
                Criteria.where("owner").is(user.getUsername())
        );
    }


}
