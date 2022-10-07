package com.cloud.cloudclient.network;

import com.cloud.cloudclient.entity.TransferFile;
import com.cloud.common.entity.CloudFolder;

import java.io.File;

public interface Connection {

    void sendLogin(String login, String password);

    void sendCredentialsForRegistration(String login, String password);

    void deleteFile(String filePathToServer);

    void renameFile(String path, String oldName, String newName);

    void authWithToken(String token, String username);

    void requestFile(String path);

    void requestStructure();

    void sendFile(File file);

    void sendFileForFolder(File file, String pathForServer);

    void sendFolder(CloudFolder cloudFolder);

    void requestFileForFolder(String pathServer, String pathClient);

    void createFolder(String path);

    void moveFile(TransferFile transferFile, String folderPath);

    void copyFile(TransferFile transferFile, String folderPath);

    void disconnect();
}
