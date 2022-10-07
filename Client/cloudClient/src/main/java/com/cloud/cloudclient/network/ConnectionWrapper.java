package com.cloud.cloudclient.network;

import com.cloud.cloudclient.entity.TransferFile;
import com.cloud.common.entity.CloudFolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.concurrent.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionWrapper implements Connection {
    @Getter
    private static ConnectionWrapper INSTANCE = new ConnectionWrapper();
    private BlockingQueue<Runnable> queue = new SynchronousQueue<>();
    private ExecutorService executorService = new ThreadPoolExecutor(1, 5, 30, TimeUnit.SECONDS, queue);

    @Override
    public void sendLogin(String login, String password) {
        ConnectionUtil.get().sendLogin(login, password);
    }

    @Override
    public void sendCredentialsForRegistration(String login, String password) {
        ConnectionUtil.get().sendCredentialsForRegistration(login, password);
    }

    @Override
    public void deleteFile(String filePathToServer) {
        executorService.submit(() -> ConnectionUtil.get().deleteFile(filePathToServer));
    }

    @Override
    public void renameFile(String path, String oldName, String newName) {
        executorService.submit(() -> ConnectionUtil.get().renameFile(path, oldName, newName));
    }

    @Override
    public void authWithToken(String token, String username) {
        ConnectionUtil.get().authWithToken(token, username);
    }

    @Override
    public void requestFile(String path) {
        executorService.submit(() -> ConnectionUtil.get().requestFile(path));
    }

    @Override
    public void requestStructure() {
        ConnectionUtil.get().requestStructure();
    }

    @Override
    public void sendFile(File file) {
        executorService.submit(() -> ConnectionUtil.get().sendFile(file));
    }

    // FIXME: 10/6/2022 send list of files
    @Override
    public void sendFileForFolder(File file, String pathForServer) {
        executorService.submit(() -> ConnectionUtil.get().sendFileForFolder(file, pathForServer));
    }

    @Override
    public void sendFolder(CloudFolder cloudFolder) {
        executorService.submit(() -> ConnectionUtil.get().sendFolder(cloudFolder));
    }

    @Override
    public void requestFileForFolder(String pathServer, String pathClient) {
        executorService.submit(() -> ConnectionUtil.get().requestFileForFolder(pathServer, pathClient));
    }

    @Override
    public void createFolder(String path) {
        executorService.submit(() -> ConnectionUtil.get().createFolder(path));
    }

    @Override
    public void moveFile(TransferFile transferFile, String folderPath) {
        executorService.submit(() -> ConnectionUtil.get().moveFile(transferFile, folderPath));
    }

    @Override
    public void copyFile(TransferFile transferFile, String folderPath) {
        executorService.submit(() -> ConnectionUtil.get().copyFile(transferFile, folderPath));
    }

    @Override
    public void disconnect() {
        ConnectionUtil.get().disconnect();
    }
}
