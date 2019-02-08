package org.schicwp.dinky.exceptions;

/**
 * Created by will.schick on 2/8/19.
 */
public class SubmissionValidationException extends RuntimeException{

    public SubmissionValidationException(String message){
        super(message);
    }
}
