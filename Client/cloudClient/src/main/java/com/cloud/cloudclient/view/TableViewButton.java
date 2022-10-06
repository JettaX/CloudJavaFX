package com.cloud.cloudclient.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class TableViewButton extends Button {
    private String name;
    private long size;
    private ImageView imageViewCloud;
    private ImageView imageViewIcon;

    public TableViewButton(String name, long size, ImageView imageViewCloud, ImageView imageViewIcon) {
        this.name = name;
        this.size = size;
        this.imageViewCloud = imageViewCloud;
        this.imageViewIcon = imageViewIcon;
        initializer();
    }

    private void initializer(){
        var infoWrapper = new VBox();
        infoWrapper.setAlignment(Pos.CENTER);
        var imageView = imageViewIcon;
        var cloudWrapper = new VBox();
        ImageView imageCloud = imageViewCloud;
        cloudWrapper.setAlignment(Pos.BOTTOM_LEFT);
        cloudWrapper.getChildren().add(imageCloud);
        infoWrapper.getChildren().addAll(cloudWrapper, imageView, new Label(name));

        this.setGraphic(infoWrapper);
        this.setAlignment(Pos.CENTER);
        this.getStyleClass().add("doc-table-view");
        this.setTooltip(new TooltipFile(name, size));
    }
}
