package com.cloud.cloudclient.network;


import com.cloud.cloudclient.Main;
import com.cloud.cloudclient.dao.UserDAO;
import com.cloud.cloudclient.dao.UserDaoJDBC;
import com.cloud.cloudclient.utils.FilesChecker;
import com.cloud.cloudclient.utils.FileUtil;
import com.cloud.cloudclient.utils.PropertiesUtil;
import com.cloud.cloudclient.view.utils.BackUrl;
import com.cloud.common.entity.CloudFolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TCPListener implements TCPConnectionListener {
    @Getter
    private static TCPListener INSTANCE = new TCPListener();
    UserDAO userDAO = UserDaoJDBC.getINSTANCE();

    public static void createNewInstance() {
        INSTANCE = new TCPListener();
    }

    {
        log.debug("TCPListener created");
        createConnections();
    }

    @Override
    public void onConnected(String login) {

    }

    @Override
    public void onDisconnect(String login) {
        Connection.remove();
        log.info("Disconnected from server");
    }

    @Override
    public void onException(Exception e) {
        log.warn(e.getMessage());
        Connection.remove();
    }

    @Override
    public void onRequestFile(String fileName) {

    }

    @Override
    public void onReceiveFile(String fileName, long size, ByteBuf buf) {
        log.info("Received file" + fileName);
        try {
            DownloadUtil.downloadFile(FileUtil.saveFile(fileName), size, buf);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void onReceiveStructure(String structure) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Structure received");
            Main.root = mapper.readValue(structure, CloudFolder.class);
            FilesChecker.refillServerFiles(Main.root);
            log.debug("fill files");
            Platform.runLater(Main::reFillHierarchy);
        } catch (IOException e) {
            log.warn("Error while parsing structure", e);
        }
    }

    @Override
    public void onAttemptAuth(String message) {
        Platform.runLater(() -> Main.showError(message, BackUrl.SIGN_IN));
    }

    @Override
    public void onAuthSuccess(String login) {
        Platform.runLater(() -> {
            try {
                if (login == null) {
                    throw new IOException("Login is null");
                }
                Main.user = userDAO.getUserByUserName(login);
                PropertiesUtil.setProperty("username", login);
                Platform.runLater(() -> FilesChecker.refillLocalFiles(FileUtil.getRootFolder()));
                Main.showHome();
            } catch (RuntimeException e) {
                log.warn("Error while getting user", e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onAuthFailed(String message) {
        Platform.runLater(() -> Main.showError(message, BackUrl.SIGN_IN));
    }

    @Override
    public void onTokenUpdate(String token) {
        Main.token = token;
        PropertiesUtil.setProperty("token", token);
        PropertiesUtil.setProperty("username", Main.user.getUsername());
    }

    @Override
    public void onRequestFileForFolder(String paths) {
        log.info("Request file for folder " + paths);
        try {
            String[] path = paths.split(":sep:");
            String filePathClient = path[0];
            String filePathServer = path[1];
            File file = FileUtil.getFile(filePathClient);
            Connection.get().sendFileForFolder(file, filePathServer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onReceivedFileForFolder(String path, long size, ByteBuf buf) {
        log.info("Received file for folder " + path);
        try {
            DownloadUtil.downloadFile(FileUtil.getFile(path), size, buf);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private void createConnections() {
        try {
            Connection.addIfNotExists(this);
        } catch (IOException e) {
            log.warn("Error");
        }
    }
}
