package com.inodes.third.entity;

import java.io.Serializable;

public class UploadFile implements Serializable {
    private String filename;
    private String url;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public String getUrl() {
        return url;
    }
}
