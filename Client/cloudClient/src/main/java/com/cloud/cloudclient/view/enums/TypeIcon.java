package com.cloud.cloudclient.view.enums;

public enum TypeIcon {
    TEXT("file-text2.png"),
    ZIP("file-zip.png"),
    MUSIC("file-music.png"),
    VIDEO("file-play.png"),
    PICTURE("file-picture.png"),
    FOLDER("folder-open.png"),
    FOLDER_EMPTY("folder.png"),
    OTHER("file-empty.png");

    private String iconPath;

    TypeIcon(String iconName) {
        this.iconPath = iconName;
    }

    public String getIconPath() {
        return iconPath;
    }
}
