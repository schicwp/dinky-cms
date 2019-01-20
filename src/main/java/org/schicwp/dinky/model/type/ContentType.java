package org.schicwp.dinky.model.type;

import org.schicwp.dinky.model.Content;
import org.schicwp.dinky.workflow.Workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by will.schick on 1/4/19.
 */
public class ContentType {

    private final String name;
    private final Collection<Field> fields;
    private final Collection<String> workflows;
    private final String nameField;

    public ContentType(String name, Collection<Field> fields, Collection<String> workflows, String nameField) {
        this.name = name;
        this.fields = fields;
        this.workflows = workflows;
        this.nameField = nameField;
    }


    public ValidationResult validate(Content content){

        ValidationResult result = new ValidationResult();

        for (Field field: fields){
            List<String> fieldErrors = new ArrayList<>();

            if (field.isRequired() && !content.getContent().containsKey(field.getName())) {
                fieldErrors.add("Required");
                result.setErrors(field.getName(),fieldErrors);
            }

            if (content.getContent().containsKey(field.getName())){

                if (!field.validateSubmission(content.getContent().get(field.getName()),fieldErrors)) {
                    result.setErrors(field.getName(),fieldErrors);
                }
            }
        }

        return result;
    }

    public Content sanitize(Content content){

        List<String> fieldNames = new ArrayList<>(content.getContent().keySet());

        fieldNames.forEach( name->{
            if (this.fields.stream().noneMatch(f->f.getName().equals(name)))
                content.getContent().remove(name);
        });

        return content;
    }

    public Content convert(Content content){

        for (Field field: fields){
            if (content.getContent().containsKey(field.getName())) {
                content.getContent().put(
                        field.getName(),
                        field.convertSubmission(content.getContent().get(field.getName()),  content)
                );
            }
        }

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

    public Collection<String> getWorkflows() {
        return workflows;
    }
}
