package com.cloud.cloudclient.view.enums;

public enum TypeOfLoad {
    DOWNLOAD("download-bar"),
    UPLOAD("upload-bar");

    private String type;

    TypeOfLoad(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
