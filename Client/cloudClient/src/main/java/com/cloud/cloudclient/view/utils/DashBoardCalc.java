package com.cloud.cloudclient.view.utils;

import com.cloud.cloudclient.entity.DashBoard;
import com.cloud.cloudclient.utils.FilesChecker;
import com.cloud.cloudclient.view.enums.TypeOfFiles;
import com.cloud.common.entity.CloudFile;

public class DashBoardCalc {
    private static DashBoard dashBoard;

    public static DashBoard getDashBoard() {
        if (dashBoard == null) {
            dashBoard = new DashBoard();
            calcDashBoard();
        }
        return dashBoard;
    }

    private static void calcDashBoard() {
        FilesChecker.getListServerFiles().forEach(file -> {
            String[] split = file.getPath().split("\\.");
            var type = split[split.length - 1];
            calc(file, type);
        });
    }

    private static void calc(CloudFile file, String type) {
        if (TypeOfFiles.DOCUMENT.contains(type)) {
            dashBoard.addDocument(file.getSize());
        } else if (TypeOfFiles.MUSIC.contains(type)) {
            dashBoard.addMusic(file.getSize());
        } else if (TypeOfFiles.VIDEO.contains(type)) {
            dashBoard.addVideo(file.getSize());
        } else if (TypeOfFiles.IMAGE.contains(type)) {
            dashBoard.addImage(file.getSize());
        } else {
            dashBoard.addOther(file.getSize());
        }
    }

    public static void update(CloudFile cloudFile) {
        String[] split = cloudFile.getPath().split("\\.");
        var type = split[split.length - 1];
        calc(cloudFile, type);
    }
}
