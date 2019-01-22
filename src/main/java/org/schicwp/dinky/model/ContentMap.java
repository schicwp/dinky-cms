package org.schicwp.dinky.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by will.schick on 1/17/19.
 */
public class ContentMap extends HashMap<String,Object> {

    public ContentMap(){}

    public ContentMap(Map<String,Object> map){
        this.putAll(map);
    }

    public <T> T getAs(String key, Class<T> c){
        return (T)get(key);
    }

    public <T> T getAsOrDefault(String key, T def){

        return (T)getOrDefault(key,def);
    }


    public ContentMap getAsMap(String key){
        if (this.containsKey(key))
            return new ContentMap(this.getAs(key,Map.class));
        return null;
    }

    public ContentMap getAsMapOrDefault(String key, ContentMap contentMap){
        if (this.containsKey(key))
            return new ContentMap(this.getAs(key,Map.class));
        return contentMap;
    }

    public <T> Collection<T> getAsCollectionOf(String key , Class<T> c){
        return (Collection<T>)get(key);
    }
}
