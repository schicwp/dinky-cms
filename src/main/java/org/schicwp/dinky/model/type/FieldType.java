package org.schicwp.dinky.model.type;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.model.ContentMap;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface FieldType {

    String getName();

    boolean validateSubmission(Object object, ContentMap properties, Collection<String> errors);

    Object convertSubmission(Object input, ContentMap properties, Content owner);

}
