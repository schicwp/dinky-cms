package org.schicwp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.schicwp.api.dto.ContentSubmission;
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

    @PostMapping
    public Content postContent(
            @RequestBody ContentSubmission contentSubmission){
        return workflowExecutionService.executeAction( contentSubmission);
    }

    @PostMapping( consumes = "multipart/form-data")
    public Content postContentMultipart(
            MultipartHttpServletRequest request,
            @RequestParam("content") String content
            ) throws IOException{


                ObjectMapper objectMapper = new ObjectMapper();
                ContentSubmission contentSubmission = objectMapper.readValue(content,ContentSubmission.class);

                request.getFileNames().forEachRemaining((f)->{

                    for (String k: contentSubmission.getContent().keySet()){

                        if (f.equals(contentSubmission.getContent().get(k)))
                            contentSubmission.getContent().put(k,request.getFile(f));

                    }
                });

        return workflowExecutionService.executeAction(contentSubmission);
    }



    @GetMapping("{id}")
    public Content getContent(@PathVariable("id") String id){
        return  contentRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("{id}/history")
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

    @GetMapping
    public Page<Content> listContent(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
            ){

        params.remove("q");
        params.remove("size");
        params.remove("page");

        logger.info("Params: " + params);

        Criteria criteria = new Criteria();

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
