package org.schicwp.dinky.search;

import org.schicwp.dinky.model.Content;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by will.schick on 2/15/19.
 */
@Document(indexName = "default")
public class SearchContent extends Content {

    public SearchContent(){}

    public SearchContent(Content content){
        super(content);
    }


    Float score;


    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
