package com.cloud.cloudclient.view;

import com.cloud.cloudclient.view.utils.SizeUtil;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;


public class TooltipFile extends Tooltip {
    private String filename;
    private long size;

    public TooltipFile(String filename, long size) {
        this.filename = filename;
        this.size = size;
        initializer();
    }
    private void initializer() {
        VBox main = new VBox();

        Label fileName = new Label("file\t".concat(filename));
        main.getChildren().add(fileName);

        if (size > 0) {
            Label fileSize = new Label("size\t".concat(SizeUtil.calculateSize(size)));
            main.getChildren().add(fileSize);
        }

        this.setGraphic(main);
    }
}
