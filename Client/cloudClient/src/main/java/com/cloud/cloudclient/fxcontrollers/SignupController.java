package com.cloud.cloudclient.fxcontrollers;

import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.network.ConnectionWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class SignupController {
    public VBox formWrapper;
    public TextField inputLogin;
    public PasswordField inputPassword;
    public PasswordField inputPasswordRepeated;
    public Button loginButton;
    public Button buttonBack;
    private ConnectionWrapper connection;

    public void initialize() {
        connection = ConnectionWrapper.getINSTANCE();
        graphicsApply(buttonBack, "/images/icon/arrow-left.png", 30, 30);
    }

    private void graphicsApply(Button button, String iconPath, int width, int height) {
        button.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)), width, height, false, true)));
    }

    public void loginButtonAction(ActionEvent actionEvent) {
        if (!inputLogin.getText().isBlank() &&
                !inputPassword.getText().isBlank() &&
                !inputPasswordRepeated.getText().isBlank() &&
                inputPassword.getText().equals(inputPasswordRepeated.getText())) {
            connection.sendCredentialsForRegistration(inputLogin.getText(), inputPassword.getText());
        }
    }

    public void backListener(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.BACK)) {
            Main.showLogin();
        }
    }

    public void backButtonListener(ActionEvent keyEvent) {
        Main.showLogin();
    }
}
