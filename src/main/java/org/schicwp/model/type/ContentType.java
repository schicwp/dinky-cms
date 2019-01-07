package org.schicwp.model.type;

import org.schicwp.model.Content;
import org.schicwp.workflow.Workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by will.schick on 1/4/19.
 */
public class ContentType {

    private final String name;
    private final Collection<Field> fields;
    private final Workflow workflow;
    private final String nameField;

    public ContentType(String name, Collection<Field> fields, Workflow workflow, String nameField) {
        this.name = name;
        this.fields = fields;
        this.workflow = workflow;
        this.nameField = nameField;

        if (this.workflow == null || this.fields == null)
            throw new RuntimeException();
    }

    public Workflow getWorkflow(){
        return workflow;
    }

    public boolean validate(Content content){

        boolean ok = true;

        for (Field field: fields){
            if (field.isRequired() && !content.getContent().containsKey(field.getName()))
                ok = false;

            if (content.getContent().containsKey(field.getName())){
                ok &= field.validate(content.getContent().get(field.getName()));
            }
        }

        return ok;
    }

    public Content sanitize(Content content){

        List<String> fieldNames = new ArrayList<>(content.getContent().keySet());

        fieldNames.forEach( name->{
            if (this.fields.stream().noneMatch(f->f.getName().equals(name)))
                content.getContent().remove(name);
        });

        return content;
    }

    public String getName() {
        return name;
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public String getNameField() {
        return nameField;
    }
}
