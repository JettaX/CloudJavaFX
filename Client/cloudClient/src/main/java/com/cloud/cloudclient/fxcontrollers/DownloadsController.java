package com.cloud.cloudclient.fxcontrollers;


import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.view.Indicator;
import com.cloud.cloudclient.view.Indicators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class DownloadsController {
    @FXML
    public VBox downloadsWrapper;
    @FXML
    public Button buttonBack;

    public void initialize() {
        checkDownloads();
        graphicsApply(buttonBack, "/images/icon/arrow-left.png", 30, 30);
    }

    private void graphicsApply(Button button, String iconPath, int width, int height) {
        button.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)), width, height, false, true)));
    }

    private void checkDownloads() {
        if (Indicators.getSize() > 0) {
            Indicators.getIndicators().forEach((fileName, indicator) -> {
                if (indicator.get().getProgressBar().getProgress() >= 1.0) {
                    removeDownload(fileName, indicator.get());
                } else {
                    createDownload(fileName, indicator.get());
                }
            });
        } else {
            createEmptyDownload();
        }
    }

    private void createEmptyDownload() {
        downloadsWrapper.getChildren().clear();
        Label label = new Label("No downloads");
        label.getStyleClass().add("noDownloads");
        downloadsWrapper.getChildren().add(label);
    }

    private void createDownload(String fileName, Indicator indicator) {
        VBox downloadWrapper = new VBox();
        downloadWrapper.getStyleClass().add("downloadWrapper");
        Label downloadLabel = new Label(fileName);
        indicator.getProgressBar().getStyleClass().add(indicator.getType().getType());
        System.out.println(indicator.getType().getType());
        downloadWrapper.getChildren().addAll(downloadLabel, indicator.getProgressBar());
        downloadsWrapper.getChildren().add(downloadWrapper);
    }

    private void removeDownload(String fileName, Indicator indicator) {
        Indicators.removeIndicator(fileName);
        downloadsWrapper.getChildren().remove(indicator.getProgressBar());
    }

    @FXML
    public void backListener(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            Main.showHome();
        }
    }

    @FXML
    public void backButtonListener(ActionEvent keyEvent) {
        Main.showHome();
    }
}
