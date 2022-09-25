package com.cloud.cloudclient.fxcontrollers;


import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.network.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginController {
    @FXML
    public Button loginButton;
    @FXML
    public TextField inputLogin;
    @FXML
    public TextField inputPassword;
    @FXML
    public Button signUpButton;
    @FXML
    public VBox authWrapper;

    public void loginButtonAction() {
        String login = inputLogin.getText();
        String password = inputPassword.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        try {
            Connection.get().sendLogin(login, password);
        } catch (NullPointerException e) {
            log.warn("Server is not connected");
        }
    }

    public void signUpButtonAction(ActionEvent actionEvent) {
        Main.showSignUp();
    }
}
