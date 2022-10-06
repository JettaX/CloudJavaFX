package com.cloud.cloudclient.view;

import com.cloud.cloudclient.view.utils.SizeUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ListViewButton extends Button {
    private String name;
    private long size;
    private ImageView imageViewCloud;
    private ImageView imageViewIcon;

    public ListViewButton(String name, long size, ImageView imageViewCloud, ImageView imageViewIcon) {
        this.name = name;
        this.size = size;
        this.imageViewCloud = imageViewCloud;
        this.imageViewIcon = imageViewIcon;
        initializer();
    }

    private void initializer() {
        var infoWrapper = new HBox();
        infoWrapper.setSpacing(10);

        Label cloudState = new Label("");
        cloudState.setGraphic(imageViewCloud);

        var imageView = imageViewIcon;

        HBox info = new HBox();
        info.setSpacing(10);

        Label nameLabel = new Label(name);
        info.getChildren().add(nameLabel);

        if (size > 0) {
            Label labelSize = new Label(SizeUtil.calculateSize(size));
            labelSize.setAlignment(Pos.CENTER_RIGHT);
            info.getChildren().add(labelSize);
        }

        infoWrapper.getChildren().addAll(cloudState, imageView, info);

        this.setGraphic(infoWrapper);
        this.getStyleClass().add("doc-list-view");
    }
}
