package org.schicwp.dinky.model.type;

import org.schicwp.dinky.model.Content;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface FieldType {


    boolean validateSubmission(Object object, Map<String, Object> properties, Collection<String> errors);

    Object convertSubmission(Object input, Map<String, Object> properties, Content owner);

}
