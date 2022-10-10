package com.cloud.cloudclient.fxcontrollers;


import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.view.enums.BackUrl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.Setter;

public class ErrorController {
    @Setter
    private BackUrl back;
    @FXML
    public Label errorLabel;

    @FXML
    public Button okButton;

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }

    public void handleOkButton(ActionEvent event) {
        okButton.getScene().getWindow().hide();
        if (back != null) {
            if (back.equals(BackUrl.SIGN_IN)) {
                Main.showLogin();
            }
            if (back.equals(BackUrl.SIGN_UP)) {
                /*HelloApplication.showSignUp();*/
            }
        } else {
            Main.showLogin();
        }

    }
}
