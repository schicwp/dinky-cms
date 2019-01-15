package org.schicwp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.schicwp.model.Content;
import org.schicwp.persistence.ContentService;
import org.schicwp.workflow.WorkflowExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/4/19.
 */
@RestController
@RequestMapping("/api/v1/content")
public class ContentResource {

    private static final Logger logger = Logger.getLogger(ContentResource.class.getCanonicalName());


    @Autowired
    ContentService contentRepository;

    @Autowired
    WorkflowExecutionService workflowExecutionService;

    @PostMapping("{type}")
    public Content postContent(
            @PathVariable("type") String type,
            @RequestBody ContentSubmission contentSubmission){
        return workflowExecutionService.executeAction(Optional.empty(), contentSubmission, type);
    }

    @PostMapping(value = "{type}", consumes = "multipart/form-data")
    public Content postContentMultipart(
            @PathVariable("type") String type,
            MultipartHttpServletRequest request,
            @RequestParam("content") String content
            ) throws IOException{


                ObjectMapper objectMapper = new ObjectMapper();
                ContentSubmission contentSubmission = objectMapper.readValue(content,ContentSubmission.class);



                System.out.println(content);

                request.getFileNames().forEachRemaining((f)->{

                    for (String k: contentSubmission.getContent().keySet()){

                        if (f.equals(contentSubmission.getContent().get(k)))
                            contentSubmission.getContent().put(k,request.getFile(f));

                    }

                    MultipartFile multipartFile = request.getFile(f);
                    System.out.println(multipartFile.getContentType());
                    System.out.println(multipartFile.getName());
                    System.out.println(multipartFile.getOriginalFilename());

                    System.out.println(f);

                });
        System.out.println("In Controller: " + request);

        //files.keySet().forEach(f -> System.out.println(f));

        return workflowExecutionService.executeAction(Optional.empty(), contentSubmission, type);
    }



    @PutMapping("{type}/{id}")
    public Content postContent(
            @PathVariable("type") String type,
            @PathVariable("id") String id,
            @RequestBody ContentSubmission contentSubmission){
       return workflowExecutionService.executeAction(Optional.of(id),contentSubmission,type);
    }

    @GetMapping("item/{id}")
    public Content getContent(@PathVariable("id") String id){
        return  contentRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("item/{id}/history")
    public Page<Content> getContentHistory(
            @PathVariable("id") String id,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page
    ){
        return  contentRepository.getHistory(id,
                    PageRequest.of(page,size,
                            Sort.by("content.modified").descending()
                    )
        );
    }

    @GetMapping("type/{type}")
    public Page<Content> listContent(
            @PathVariable("type") String type,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
            ){

        params.remove("q");
        params.remove("size");
        params.remove("page");

        logger.info("Params: " + params);

        Criteria criteria = Criteria.where("type").is(type);

        for (String k:params.keySet()){
                criteria = criteria.and(k).is(params.get(k));
        }

        Query query;

        if (q != null)
            query = new BasicQuery(q).addCriteria(criteria);
        else
            query = Query.query(criteria);

        return contentRepository.find(
                query,
                PageRequest.of(page,size,
                    Sort.by("modified").descending()
                )
        );
    }



    @GetMapping("type/{type}/{state}")
    public Page<Content> listContent(
            @PathVariable("type") String type,
            @PathVariable("state") String state,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params){


        params.remove("q");
        params.remove("size");
        params.remove("page");

        logger.info("Params: " + params);

        Criteria criteria =
                Criteria.where("type").is(type)
                .and("state").is(state);

        for (String k:params.keySet()){
                criteria = criteria.and(k).is(params.get(k));
        }

        Query query;

        if (q != null)
            query = new BasicQuery(q).addCriteria(criteria);
        else
            query = Query.query(criteria);

        return contentRepository.find(
                query,
                PageRequest.of(page,size,
                        Sort.by("modified").descending()
                )
        );
    }






}
