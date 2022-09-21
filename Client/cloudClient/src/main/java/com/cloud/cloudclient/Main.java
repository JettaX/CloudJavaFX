package com.cloud.cloudclient;


import com.cloud.cloudclient.entity.User;
import com.cloud.cloudclient.fxcontrollers.ErrorController;
import com.cloud.cloudclient.fxcontrollers.HomeController;
import com.cloud.cloudclient.network.TCPListener;
import com.cloud.cloudclient.view.ActiveScreen;
import com.cloud.cloudclient.view.utils.BackUrl;
import com.cloud.common.entity.CloudFolder;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main extends Application {
    public static User user;
    public static CloudFolder root;
    public static String token;
    private static Stage stage;
    private static HomeController homeController;
    private static ActiveScreen activeScreen;
    public static boolean isServerConnected = false;

    @Getter
    private HostServices hostService = getHostServices();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setResizable(true);
        stage.setTitle("Box");
        stage.setWidth(800);
        stage.setHeight(600);
        TCPListener.createNewInstance();
        showLogin();
    }

    public static void showError(String message, BackUrl back) {
        activeScreen = ActiveScreen.ERROR;
        FXMLLoader fxmlLoader = createStage("error.fxml");
        ErrorController errorController = fxmlLoader.getController();
        errorController.setBack(back);
        errorController.setErrorMessage(message);
    }

    public static void showLogin() {
        activeScreen = ActiveScreen.LOGIN;
        stage.close();
        createStage("login.fxml");
    }

    public static void showSignUp() {
        activeScreen = ActiveScreen.SIGNUP;
        stage.close();
        createStage("signup.fxml");
    }

    public static void showHome() {
        activeScreen = ActiveScreen.HOME;
        stage.close();
        FXMLLoader loader = createStage("home.fxml");
        homeController = loader.getController();
        homeController.initializer();
    }

    public static void showDialog(String description) {
        PopupControl popup = new PopupControl();
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);
        popup.setConsumeAutoHidingEvents(true);

        VBox commonWrapper = new VBox();
        commonWrapper.getStylesheets().add(Main.class.getResource("styles/common.css").toExternalForm());
        commonWrapper.setFillWidth(true);
        commonWrapper.getStyleClass().add("popup-rename-wrapper");
        Label descriptionAction = new Label(description);
        descriptionAction.getStyleClass().add("popup-label");
        TextField inputName = new TextField();
        inputName.getStyleClass().addAll("input-color", "popup-rename-input");
        commonWrapper.getChildren().addAll(descriptionAction, inputName);

        popup.getStyleClass().add("popup-rename");
        popup.getScene().setRoot(commonWrapper);
        var width = popup.getWidth() / 2;
        var height = popup.getHeight() / 2;

        var windowWidth = stage.getWidth() / 2;
        var windowHeight = stage.getHeight() / 2;

        var x = stage.getX() + windowWidth - width;
        var y = stage.getY() + windowHeight - height;

        popup.show(stage, x, y);
    }

    public static void reFillHierarchy() {
        if (activeScreen.equals(ActiveScreen.HOME)) {
            homeController.generateHierarchy(root);
        }
    }

    public static void showDownloads() {
        activeScreen = ActiveScreen.DOWNLOADS;
        stage.close();
        createStage("downloads.fxml");
    }

    private static FXMLLoader createStage(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), stage.getWidth() - 16, stage.getHeight() - 39);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double x = stage.getX();
        double y = stage.getY();
        stage.setScene(scene);
        stage.setX(x);
        stage.setY(y);
        stage.show();
        return fxmlLoader;
    }

    public static void disconnect() {
        TCPListener.createNewInstance();
        isServerConnected = false;
        user = null;
        token = null;
        root = null;
        Platform.runLater(Main::showLogin);
    }
}