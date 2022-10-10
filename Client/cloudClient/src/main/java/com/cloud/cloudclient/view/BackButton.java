package com.cloud.cloudclient.view;


import com.cloud.cloudclient.view.enums.BackUrl;
import javafx.scene.control.Button;


public class BackButton extends Button {
    private BackUrl backUrl;

    public BackButton(BackUrl backUrl) {
        super("Back");
        this.backUrl = backUrl;
        initializer();
    }

    private void initializer() {
        this.setOnAction(event -> {
           /* if (backUrl.equals(BackUrl.LOGIN)) {
                *//*HelloApplication.showChats(Main.user);*//*
            } else if (backUrl.equals(BackUrl.LOGIN)) {
                Main.showLogin();
            }*/
        });
        this.getStyleClass().add("backButton");
    }
}
