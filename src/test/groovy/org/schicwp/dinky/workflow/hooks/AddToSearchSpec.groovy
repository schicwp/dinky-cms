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
class AddToSearchSpec extends Specification{


    AddToSearch addToSearch;

    SearchService searchService;

    SearchRepository searchRepository;

    void setup(){

        searchService = Mock(SearchService)
        searchRepository = Mock(SearchRepository)

        addToSearch = new AddToSearch(
                searchService: searchService
        )
    }

    void "the name should be correct"(){
        expect:
        addToSearch.name == "AddToSearch"
    }

    void "it should add the item to the search index specified in the config"(){
        given:
        "a hook instatnce"
        ActionHook hook = addToSearch.createActionHook(new ContentMap(
                [index:"arf"]
        ))

        and:
        "some content"
        Content content = new Content(version: 13)

        when:
        "the hook is called"
        hook.execute(content,new ContentMap())

        then:
        "the search services should be called to set index"
        1 * searchService.withIndex("arf",_) >> { String index, Consumer<SearchRepository> c->
            c.accept(searchRepository)
        }

        and:
        "the content should be saved"
        1 * searchRepository.save({Content c->
            return c.searchVersions == [
                    arf: 13
            ]
        })
    }

    void "it should use 'default' if no index specified"(){
        given:
        "a hook instatnce"
        ActionHook hook = addToSearch.createActionHook(new ContentMap(
        ))

        and:
        "some content"
        Content content = new Content(version: 13)

        when:
        "the hook is called"
        hook.execute(content,new ContentMap())

        then:
        "the search services should be called to set index"
        1 * searchService.withIndex("default",_) >> { String index, Consumer<SearchRepository> c->
            c.accept(searchRepository)
        }

        and:
        "the content should be saved"
        1 * searchRepository.save({Content c->
            return c.searchVersions == [
                    default: 13
            ]
        })
    }
}
