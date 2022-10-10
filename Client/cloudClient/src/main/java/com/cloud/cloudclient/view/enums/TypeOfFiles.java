package com.cloud.cloudclient.view.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TypeOfFiles {
    IMAGE("jpg", "png", "gif", "bmp", "tif"),
    VIDEO("mp4", "wmv", "avi", "mkv", "mpeg", "flv"),
    MUSIC("mp3", "wav", "midi", "aac"),
    DOCUMENT("doc", "docx", "txt", "ppt", "pdf", "pptx", "djvu", "xls", "xlsx");

    @Getter
    private List<String> values = new ArrayList<>();

    public boolean contains(String type) {
        return values.contains(type);
    }
    TypeOfFiles(String... types) {
        values.addAll(Arrays.asList(types));
    }
}
