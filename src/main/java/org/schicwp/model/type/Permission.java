package org.schicwp.model.type;

/**
 * Created by will.schick on 1/5/19.
 */
public enum Permission {
    RW,R,W,NONE;

    public static Permission fromString(String s){
        if (s == null)
            return null;

        s = s.trim().toLowerCase();

        switch (s){
            case "rw": return RW;
            case "r": return R;
            case "w": return W;
            case "none":
            case "":
            case "-":
                return NONE;

        }

        return null;
    }


}
