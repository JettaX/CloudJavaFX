package com.cloud.cloudclient.network;


import com.cloud.cloudclient.Main;
import com.cloud.common.entity.CloudFolder;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.SHAUtils;
import com.cloud.common.util.ServerCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Slf4j
public class TCPConnection {
    private final TCPConnectionListener eventListener;
    @Setter
    private Optional<Channel> channel;

    public TCPConnection(TCPConnectionListener eventListener) {
        this.eventListener = eventListener;
        new Thread(() -> new ClientNetty().start(this)).start();
    }

    public synchronized void sendLogin(String login, String password) {
        log.debug("Send login: {}", login);
        auth(ServerCommand.AUTH_WITH_PASSWORD, login, password);
    }

    public synchronized void sendCredentialsForRegistration(String login, String password) {
        log.debug("Send login: {}", login);
        auth(ServerCommand.AUTH_SIGN_UP, login, password);
    }

    private synchronized void auth(ServerCommand command, String login, String password) {
        CommandPacket commandPacket = CommandPacket.builder()
                .command(command)
                .username(login)
                .body(SHAUtils.SHA256(password))
                .build();
        writeToServer(commandPacket);
    }

    public synchronized void deleteFile(String filePathToServer) {
        log.debug("Delete file: {}", filePathToServer);
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.DELETE_FILE)
                .body(filePathToServer)
                .build();
        writeToServerWithCredentials(commandPacket);
    }

    public synchronized void renameFile(String path, String oldName, String newName) {
        log.debug("Rename file: {}", oldName);
        FilePacket filePacket = FilePacket.builder()
                .fileName(oldName)
                .filePath(path)
                .build();

        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.RENAME_FILE)
                .object(filePacket)
                .body(newName)
                .build();

        writeToServerWithCredentials(commandPacket);
    }

    public synchronized void authWithToken(String token, String username) {
        log.debug("Auth with token");
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.AUTH_WITH_TOKEN)
                .username(username)
                .token(token)
                .build();
        writeToServer(commandPacket);
    }

    public synchronized void requestFile(String path) {
        log.debug("Request file: {}", path);
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.REQUEST_FILE)
                .body(path)
                .build();
        writeToServerWithCredentials(commandPacket);
    }

    public synchronized void requestStructure() {
        log.debug("Request structure");
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.REQUEST_STRUCTURE)
                .build();
        writeToServerWithCredentials(commandPacket);
    }

    public synchronized void sendFile(File file) {
        try {
            log.debug("Send file: {}", file.getName());
            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.RECEIVE_FILE)
                    .username(Main.user.getUsername())
                    .token(Main.token)
                    .build();

            FilePacket filePacket = FilePacket.builder()
                    .fileName(file.getName())
                    .fileSize(Files.size(file.toPath()))
                    .build();

            DownloadUtil.uploadFile(commandPacket, filePacket, file, channel.orElseThrow());
        } catch (IOException e) {
            eventListener.onException(e);
            disconnect();
        }
    }

    public synchronized void sendFileForFolder(File file, String pathForServer) {
        try {
            log.debug("Send file for folder: {}", file.getName());

            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.RECEIVE_FILE_FOR_FOLDER)
                    .token(Main.token)
                    .username(Main.user.getUsername())
                    .build();

            FilePacket filePacket = FilePacket.builder()
                    .filePath(pathForServer)
                    .fileSize(Files.size(file.toPath()))
                    .build();

            DownloadUtil.uploadFile(commandPacket, filePacket, file, channel.orElseThrow());

        } catch (IOException e) {
            eventListener.onException(e);
            disconnect();
        }
    }

    public synchronized void sendFolder(CloudFolder cloudFolder) {
        try {
            log.debug("Send folder: {}", cloudFolder.getName());
            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.RECEIVE_FOLDER)
                    .body(new ObjectMapper().writeValueAsString(cloudFolder))
                    .build();
            writeToServerWithCredentials(commandPacket);
        } catch (IOException e) {
            eventListener.onException(e);
            disconnect();
        }
    }

    public void requestFileForFolder(String pathServer, String pathClient) {
        log.debug("Request file for folder: {}", pathServer);
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.REQUEST_FILE_FOR_FOLDER)
                .body(pathServer.concat(":sep:").concat(pathClient))
                .build();
        writeToServerWithCredentials(commandPacket);
    }

    private synchronized void writeToServerWithCredentials(CommandPacket commandPacket) {
        commandPacket.setToken(Main.token);
        commandPacket.setUsername(Main.user.getUsername());
        writeToServer(commandPacket);
    }

    public synchronized void createFolder(String path) {
        log.debug("Request for create folder: {}", path);
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.CREATE_FOLDER)
                .body(path)
                .build();
        writeToServerWithCredentials(commandPacket);
    }

    private synchronized void writeToServer(CommandPacket commandPacket) {
        if (channel.isPresent() && channel.get().isWritable()) {
            channel.get().writeAndFlush(commandPacket);
            log.debug("sent to server");
        } else {
            log.debug("Channel is not ready");
        }
    }


    public synchronized void disconnect() {
        log.debug("Disconnect");
        channel.orElseThrow().close();
        channel = Optional.empty();
    }
}
