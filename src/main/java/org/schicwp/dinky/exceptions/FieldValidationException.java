package org.schicwp.dinky.exceptions;

import org.schicwp.dinky.model.type.ValidationResult;

/**
 * Created by will.schick on 1/18/19.
 */
public class FieldValidationException extends RuntimeException {

    private final ValidationResult validationResult;

    public FieldValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
