package com.cloud.cloudclient.view.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WindowUtil {

    public static double getWidthCenter(Scene scene, Stage stage) {
        var width = scene.getWidth() / 2;
        var windowWidth = stage.getWidth() / 2;
        return stage.getX() + windowWidth - width;
    }

    public static double getHeightCenter(Scene scene, Stage stage) {
        var height = scene.getHeight() / 2;
        var windowHeight = stage.getHeight() / 2;
        return stage.getY() + windowHeight - height;
    }
}
