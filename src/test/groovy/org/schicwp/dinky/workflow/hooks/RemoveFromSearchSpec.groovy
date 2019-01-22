package org.schicwp.dinky.workflow.hooks

import org.schicwp.dinky.model.Content
import org.schicwp.dinky.model.ContentMap
import org.schicwp.dinky.search.SearchRepository
import org.schicwp.dinky.search.SearchService
import org.schicwp.dinky.workflow.ActionHook
import spock.lang.Specification

import java.util.function.Consumer

/**
 * Created by will.schick on 1/22/19.
 */
class RemoveFromSearchSpec extends Specification{


    RemoveFromSearch removeFromSearch;

    SearchService searchService;

    SearchRepository searchRepository;

    void setup(){

        searchService = Mock(SearchService)
        searchRepository = Mock(SearchRepository)

        removeFromSearch = new RemoveFromSearch(
                searchService: searchService
        )
    }

    void "the name should be correct"(){
        expect:
        removeFromSearch.name == "RemoveFromSearch"
    }

    void "it should add the item to the search index specified in the config"(){
        given:
        "a hook instatnce"
        ActionHook hook = removeFromSearch.createActionHook(new ContentMap(
                [index:"arf"]
        ))

        and:
        "some content"
        Content content = new Content(
                id:"123455",
                version: 13,
            searchVersions:[
                    arf:12
            ]
        )

        and:
        "some indexed content"
        Content indexed = new Content()

        when:
        "the hook is called"
        hook.execute(content,new ContentMap())

        then:
        "the search services should be called to set index"
        1 * searchService.withIndex("arf",_) >> { String index, Consumer<SearchRepository> c->
            c.accept(searchRepository)
        }

        and:
        "the content should be found"
        1 * searchRepository.findById("123455") >> Optional.of(indexed)

        and:
        "the content should be deleted"
        1 * searchRepository.delete(indexed)

        and:
        "the search version should be removed"
        content.searchVersions == [:]
    }

    void "it should use 'default' if no index is provided"(){
        given:
        "a hook instatnce"
        ActionHook hook = removeFromSearch.createActionHook(new ContentMap(
        ))

        and:
        "some content"
        Content content = new Content(
                id:"123455",
                version: 13,
                searchVersions:[
                        default:12
                ]
        )

        and:
        "some indexed content"
        Content indexed = new Content()

        when:
        "the hook is called"
        hook.execute(content,new ContentMap())

        then:
        "the search services should be called to set index"
        1 * searchService.withIndex("default",_) >> { String index, Consumer<SearchRepository> c->
            c.accept(searchRepository)
        }

        and:
        "the content should be found"
        1 * searchRepository.findById("123455") >> Optional.of(indexed)

        and:
        "the content should be deleted"
        1 * searchRepository.delete(indexed)

        and:
        "the search version should be removed"
        content.searchVersions == [:]
    }

}
