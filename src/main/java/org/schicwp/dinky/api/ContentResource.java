package org.schicwp.dinky.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.schicwp.dinky.api.dto.ContentSubmission;
import org.schicwp.dinky.auth.AuthService;
import org.schicwp.dinky.auth.User;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.exceptions.ValidationException;
import org.schicwp.dinky.model.type.ValidationResult;
import org.schicwp.dinky.content.ContentService;
import org.schicwp.dinky.workflow.WorkflowExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by will.schick on 1/4/19.
 */
@RestController
@RequestMapping("/api/v1")
public class ContentResource {

    private static final Logger logger = Logger.getLogger(ContentResource.class.getCanonicalName());


    @Autowired
    ContentService contentRepository;

    @Autowired
    WorkflowExecutionService workflowExecutionService;

    @PostMapping("content")
    public Content postContent(
            @RequestBody ContentSubmission contentSubmission){
        return workflowExecutionService.executeAction( contentSubmission);
    }

    @PostMapping( value = "content",consumes = "multipart/form-data")
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



    @GetMapping("content/{id}")
    public Content getContent(@PathVariable("id") String id){
        return  contentRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @GetMapping("content/{id}/history")
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

    @GetMapping("content")
    public Page<Content> listContent(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
            ){

        Query query = generateQueryFromParams(q, params);

        return contentRepository.find(
                query,
                PageRequest.of(page,size,
                    Sort.by("modified").descending()
                )
        );
    }



    @GetMapping("assigned")
    public Page<Content> assignedContent(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ) {
        Query query = generateQueryFromParams(q,params);

        return contentRepository.findAssigned(
                query,
                PageRequest.of(page,size,
                        Sort.by("modified").descending()
                )
        );
    }



    @GetMapping("mine")
    public Page<Content> myCountent(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ) {
        Query query = generateQueryFromParams(q, params);

        return contentRepository.findMine(
                query,
                PageRequest.of(page,size,
                        Sort.by("modified").descending()
                )
        );
    }


    @GetMapping("content-count")
    public long contentCount(
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ){

        Query query = generateQueryFromParams(q,params);

        return contentRepository.count(
                query
        );
    }

    @GetMapping("assigned-count")
    public long assignedCount(
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ){

        Query query = generateQueryFromParams(q,params);

        return contentRepository.countAssigned(
                query
        );
    }

    @GetMapping("mine-count")
    public long mineCount(
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ){

        Query query = generateQueryFromParams(q,params);

        return contentRepository.countOwned(
                query
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationResult> validationException(ValidationException e) {

        return new ResponseEntity<>(
                e.getValidationResult(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST
        );

    }

    private Query generateQueryFromParams(@RequestParam(value = "q", required = false) String q, @RequestParam Map<String, String> params) {
        params.remove("q");
        params.remove("size");
        params.remove("page");

        Criteria criteria = new Criteria();

        for (String k:params.keySet()){
            criteria = criteria.and(k).is(params.get(k));
        }

        Query query;

        if (q != null)
            query = new BasicQuery(q).addCriteria(criteria);
        else
            query = Query.query(criteria);
        return query;
    }











}
