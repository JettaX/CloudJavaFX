package com.cloud.cloudclient.fxcontrollers;

import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.dao.UserDAO;
import com.cloud.cloudclient.dao.UserDaoJDBC;
import com.cloud.cloudclient.entity.User;
import com.cloud.cloudclient.network.Connection;
import com.cloud.cloudclient.utils.PropertiesUtil;
import com.cloud.cloudclient.view.utils.RoundPicture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class AuthToken {
    @FXML
    public Button authButton;
    @FXML
    public HBox imageWrapper;
    @FXML
    public Button loginPasswordAuth;
    private UserDAO userDAO;
    private String username;
    private String token;

    public void initialize() {
        userDAO = UserDaoJDBC.getINSTANCE();
        username = PropertiesUtil.getProperty("username");
        token = PropertiesUtil.getProperty("token");
        User user = userDAO.getUserByUserName(username);
        imageWrapper.getChildren().add(RoundPicture.getRoundPicture(50, user.getImageUrl()));
    }

    public void auth(ActionEvent event) {
        Connection.get().authWithToken(token, username);
    }

    public void loginPasswordAuth(ActionEvent event) {
        Main.showLogin();
    }
}
