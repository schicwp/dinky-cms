package org.schicwp.dinky.model.type;

/**
 * Created by will.schick on 1/14/19.
 */
public interface FieldTypeFactory {

    FieldType createFieldType();

    String getName();
}
