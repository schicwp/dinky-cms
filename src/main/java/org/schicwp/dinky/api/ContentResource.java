package org.schicwp.dinky.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.schicwp.dinky.api.dto.ContentSubmission;
import org.schicwp.dinky.exceptions.OptimisticLockingException;
import org.schicwp.dinky.exceptions.PermissionException;
import org.schicwp.dinky.exceptions.SubmissionValidationException;
import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.exceptions.FieldValidationException;
import org.schicwp.dinky.model.type.ValidationResult;
import org.schicwp.dinky.content.ContentService;
import org.schicwp.dinky.content.ContentSubmissionService;
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

import javax.validation.Valid;
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
    ContentSubmissionService contentSubmissionService;

    @PostMapping("content")
    public Content postContent(@Valid @RequestBody ContentSubmission contentSubmission){
        return contentSubmissionService.processSubmission( contentSubmission);
    }

    @PostMapping( value = "content",consumes = "multipart/form-data")
    public Content postContentMultipart(
            MultipartHttpServletRequest request,
            @RequestParam("content") String content) throws IOException{

        ContentSubmission contentSubmission = new ObjectMapper().readValue(content,ContentSubmission.class);

        request.getFileNames().forEachRemaining((f)->{
            for (String k: contentSubmission.getContent().keySet()){
                if (f.equals(contentSubmission.getContent().get(k)))
                    contentSubmission.getContent().put(k,request.getFile(f));
            }
        });

        return contentSubmissionService.processSubmission(contentSubmission);
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

    @GetMapping("content/{id}/history/{version}")
    public Content getContentHistoryVersion(
            @PathVariable("id") String id,
            @PathVariable("version") int version
    ){
        return  contentRepository.getHistoricalVersion(id,version);
    }

    @PostMapping("content/{id}/history/{version}")
    public Content revertToVersion(
            @PathVariable("id") String id,
            @PathVariable("version") int version
    ){
        return  contentRepository.revertToHistoricalVersion(id,version);
    }

    @GetMapping("content")
    public Page<Content> listContent(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
            ){
        return contentRepository.find(
                generateQueryFromParams(q,params),
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
        return contentRepository.findAssigned(
                generateQueryFromParams(q,params),
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
        return contentRepository.findMine(
                generateQueryFromParams(q,params),
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
        return contentRepository.count(generateQueryFromParams(q,params));
    }

    @GetMapping("assigned-count")
    public long assignedCount(
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ){
        return contentRepository.countAssigned(generateQueryFromParams(q,params));
    }

    @GetMapping("mine-count")
    public long mineCount(
            @RequestParam(value = "q",required = false) String q,
            @RequestParam Map<String,String> params
    ){
        return contentRepository.countOwned(generateQueryFromParams(q,params));
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ValidationResult> validationException(FieldValidationException e) {
        return new ResponseEntity<>(e.getValidationResult(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OptimisticLockingException.class)
    public ResponseEntity<?> validationException(OptimisticLockingException e) {
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<?> validationException(PermissionException e) {
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SubmissionValidationException.class)
    public ResponseEntity<?> validationException(SubmissionValidationException e) {
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.BAD_REQUEST);
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
