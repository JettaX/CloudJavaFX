package com.cloud.cloudclient.view;

import com.cloud.cloudclient.Main;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class PopupControlRename extends PopupControl {
    private String description;
    @Getter
    private TextField inputName;
    private String inputText;

    public PopupControlRename(String description, String inputText) {
        this.description = description;
        this.inputText = inputText;
        initialize();
    }

    private void initialize() {
        this.setAutoHide(true);
        this.setAutoFix(true);
        this.setHideOnEscape(true);
        this.setConsumeAutoHidingEvents(true);

        createCustomGraphics();
    }

    private void createCustomGraphics() {
        VBox commonWrapper = new VBox();
        commonWrapper.getStylesheets().add(Main.class.getResource("styles/common.css").toExternalForm());
        commonWrapper.setFillWidth(true);
        commonWrapper.getStyleClass().add("popup-rename-wrapper");

        Label descriptionAction = new Label(description);
        descriptionAction.getStyleClass().add("popup-label");

        inputName = new TextField();
        inputName.setText(inputText);
        inputName.getStyleClass().addAll("input-color", "popup-rename-input");

        commonWrapper.getChildren().addAll(descriptionAction, inputName);
        this.getStyleClass().add("popup-rename");
        this.getScene().setRoot(commonWrapper);
    }
}
