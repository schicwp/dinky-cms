package org.schicwp.dinky.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by will.schick on 1/14/19.
 */
@Document
public class Permission {

    @Indexed
    private boolean read = false;
    @Indexed
    private boolean write = true;



    public Permission(){}

    public Permission(boolean read, boolean write){
        this.read = read;
        this.write = write;
    }

    public boolean isRead() {
        return read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }
}
