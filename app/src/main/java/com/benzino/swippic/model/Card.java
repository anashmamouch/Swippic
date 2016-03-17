package com.benzino.swippic.model;

import java.io.Serializable;

/**
 * Created on 17/3/16.
 *
 * @author Anas
 */
public class Card implements Serializable {
    private String path;
    private String size;

    public Card(String path, String size) {
        this.path = path;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
