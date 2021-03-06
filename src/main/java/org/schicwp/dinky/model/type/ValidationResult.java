package org.schicwp.dinky.model.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by will.schick on 1/18/19.
 */
public class ValidationResult {

    private boolean valid = true;

    private Map<String,Collection<String>> fieldErrors = new HashMap<>();


    public void setErrors(String field, Collection<String> errors){
        this.valid = false;
        fieldErrors.put(field,errors);
    }

    public Map<String, Collection<String>> getFieldErrors() {
        return fieldErrors;
    }

    public boolean isValid() {
        return valid;
    }
}
