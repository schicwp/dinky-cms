package org.schicwp.dinky.model;

import java.util.HashMap;

/**
 * Created by will.schick on 1/17/19.
 */
public class ContentMap extends HashMap<String,Object> {

    <T> T getAs(String key, Class<T> c){
        return (T)get(key);
    }
}
