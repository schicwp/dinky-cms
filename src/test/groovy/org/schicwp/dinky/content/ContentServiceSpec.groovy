package org.schicwp.dinky.content

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentHistory
import org.schicwp.dinky.persistence.ContentHistoryRepository
import org.schicwp.dinky.persistence.ContentRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.repository.support.PageableExecutionUtils
import spock.lang.Specification

/**
 * Created by will.schick on 1/19/19.
 */
class ContentServiceSpec extends Specification{

    ContentService contentService

    ContentRepository contentRepository;
    ContentHistoryRepository contentHistoryRepository;
    MongoTemplate mongoTemplate;
    PermissionService permissionService;
    QueryTotalCache queryTotalCache;

    Pageable pageable;

    void setup(){
        contentRepository = Mock(ContentRepository)
        contentHistoryRepository = Mock(ContentHistoryRepository)
        mongoTemplate = Mock(MongoTemplate)
        permissionService = Mock(PermissionService)
        queryTotalCache = Mock(QueryTotalCache)

        contentService = new ContentService(
                contentRepository: contentRepository,
                contentHistoryRepository: contentHistoryRepository,
                mongoTemplate: mongoTemplate,
                permissionService: permissionService,
                queryTotalCache: queryTotalCache
        )

        pageable = Mock(Pageable)
    }

    void "save should invalidate the query cache, save a historical version and save the content"(){
        given:
        "some content"
        Content content = new Content();
        and:
        "a saved version"
        Content saved = new Content();

        when:
        "the content is saved"
        def result = contentService.save(content)

        then:
        "the cache should be invalidated"
        1 * queryTotalCache.invalidate()

        and:
        "the new item should be saved to history"
        1 * contentHistoryRepository.save({ContentHistory contentHistory->
            contentHistory.content == content
        })

        and:
        "the content should be saved"
        1 * contentRepository.save(content) >> saved

        and:
        "the saved item should be returned"
        saved == result
    }

    void "getHistory should throw an exception if the item is not found"(){

        when:
        "get history is called"
        contentService.getHistory("test",pageable)

        then:
        "the item should be fetched"
        1 * contentRepository.findById("test") >> Optional.empty()

        and:
        thrown(Exception)
    }

    void "getHistory should throw an  if the item does not pass permission check"(){

        given:
        "some content"
        Content content = new Content();

        when:
        "get history is called"
        contentService.getHistory("test",pageable)

        then:
        "the item should be fetched"
        1 * contentRepository.findById("test") >> Optional.of(content)

        and:
        "the perms should be checked"
        1 * permissionService.allowRead(content) >> false

        and:
        thrown(Exception)
    }

    void "getHistory should return history"(){

        given:
        "some content"
        Content content = new Content();

        and:
        "some old versions"
        Content v1 = new Content();
        Content v2 = new Content();

        _*pageable.offset >> 0
        _*pageable.pageSize >> 10
        _*pageable.toOptional() >> Optional.of(pageable)

        when:
        "get history is called"
        def result = contentService.getHistory("test",pageable)

        then:
        "the item should be fetched"
        1 * contentRepository.findById("test") >> Optional.of(content)

        and:
        "the perms should be checked"
        1 * permissionService.allowRead(content) >> true

        and:
        "the history should be fetched"
        1 * contentHistoryRepository.findAllByContentIdOrderByContentVersionDesc("test",pageable) >> PageableExecutionUtils
                .getPage(
                    [new ContentHistory(v1),new ContentHistory(v2)],
                    pageable,
                {->100l}
        )

        and:
        result.content == [v1,v2]
    }

    void "count should use the cache"(){

        given:
        "a query"
        Query query = Mock(Query)
        Criteria permQuery = Mock(Criteria)

        when:
        "count is called"
        def result = contentService.count(query)

        then:
        "the permission service should be queried"
        1 * permissionService.getPermissionFilter() >> permQuery

        and:
        "the query should be added"
        1 * query.addCriteria(permQuery) >> query

        and:
        "the cache should be queried"
        1 * queryTotalCache.getCountForQuery(query) >> 123l

        and:
        result == 123l


    }

}
