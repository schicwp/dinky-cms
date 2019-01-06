package org.schicwp.model.type;

import java.util.Collection;
import java.util.Map;

/**
 * Created by will.schick on 1/5/19.
 */
public interface FieldType {

    String getFieldType();


    boolean validate(Object object, Map<String, String> properties, Collection<String> errors);

}
