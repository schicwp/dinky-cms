package org.schicwp.model.type;

import org.schicwp.model.Content;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface FieldType {

    String getFieldType();


    boolean validateSubmission(Object object, Map<String, String> properties, Collection<String> errors);

    Object convertSubmission(Object input, Map<String, String> properties, Content owner);

}
