package com.cloud.cloud_server.network;

import com.cloud.cloud_server.dao.UserDAO;
import com.cloud.cloud_server.dao.UserDaoJDBC;
import com.cloud.cloud_server.dao.UserSecureDAO;
import com.cloud.cloud_server.dao.UserSecureDaoJDBC;
import com.cloud.cloud_server.entity.User;
import com.cloud.cloud_server.util.FileUtil;
import com.cloud.common.entity.CloudFolder;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.ServerCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TCPListener implements TCPConnectionListener {
    private final Map<String, String> tokens;
    private ConnectionUtil connectionUtil;
    private final UserSecureDAO userSecureDAO;
    private final UserDAO userDAO;

    public TCPListener() {
        tokens = new ConcurrentHashMap<>();
        connectionUtil = new ConnectionUtil();
        userSecureDAO = UserSecureDaoJDBC.getINSTANCE();
        userDAO = UserDaoJDBC.getINSTANCE();
    }

    @Override
    public void onConnected(ChannelHandlerContext ctx, String login) {
        log.debug("Client connected {}", login);
        connectionUtil.sendStructure(ctx, FileUtil.getFolder(login));
    }

    @Override
    public void onDisconnect(ChannelHandlerContext ctx, String login) {
        log.debug("Client disconnected {}", login);
        ctx.close();
    }

    @Override
    public void onException(ChannelHandlerContext ctx, Exception e) {
        log.warn(e.getMessage());
    }

    @Override
    public void onRequestFile(ChannelHandlerContext ctx, String path) {
        log.debug("Request file {}", path);
        try {
            connectionUtil.sendFile(ctx, FileUtil.getFile(path));
        } catch (FileNotFoundException e) {
            log.warn("File not found: {}", path);
        }
    }

    @Override
    public void onAttemptAuthWithLoginPassword(ChannelHandlerContext ctx, String username, String password) {
        log.debug("Attempt auth {}", username);
        if (!userSecureDAO.isCorrectAuth(username, password)) {
            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.AUTHENTICATE_ATTEMPT)
                    .body("Password or login are incorrect")
                    .build();
            connectionUtil.writeToClient(ctx, commandPacket);
        } else {
            // TODO not secure
            tokens.put(username, new BigInteger(165, new Random()).toString(36).toUpperCase());
            onAuthSuccess(ctx, username);
            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.TOKEN_UPDATE)
                    .token(tokens.get(username))
                    .build();
            connectionUtil.writeToClient(ctx, commandPacket);
        }
    }

    @Override
    public void onAttemptAuthWithToken(ChannelHandlerContext ctx, String loginToken) {
        String login = loginToken.split(":")[0].trim();
        String token = loginToken.split(":")[1].trim();
        if (!tokens.containsKey(login) || !tokens.get(login).equals(token)) {
            onAuthFailed(ctx, "Token is incorrect");
        }
    }

    @Override
    public void onAuthSuccess(ChannelHandlerContext ctx, String login) {
        log.debug("Auth success {}", login);
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.AUTHENTICATE_SUCCESS)
                .body(login)
                .build();
        connectionUtil.writeToClient(ctx, commandPacket);
        onConnected(ctx, login);
    }

    @Override
    public void onAuthFailed(ChannelHandlerContext ctx, String message) {
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.AUTHENTICATE_FAILED)
                .body(message)
                .build();
        connectionUtil.writeToClient(ctx, commandPacket);
    }

    @Override
    public void onReceivingFile(ChannelHandlerContext ctx, CommandPacket commandPacket, FilePacket filePacket) {
        log.debug("Receive file {}", filePacket.getFileName());
        try {
            File file = FileUtil.saveFile(filePacket.getFileName(), commandPacket.getUsername());
            writeToFile(file, filePacket.getFileSize(), filePacket.getFileContent(), filePacket.isFirst());
        } catch (IOException e) {
            log.warn("Error receiving file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        /*onReceivedFile(ctx, commandPacket.getUsername());*/
    }

    @Override
    public void onReceivedFile(ChannelHandlerContext ctx, String userName) {
        log.debug("Received file {}", userName);
        connectionUtil.sendStructure(ctx, FileUtil.getFolder(userName));
    }

    @Override
    public void onRequestStructure(ChannelHandlerContext ctx, String userName) {
        log.debug("Request structure {}", userName);
        connectionUtil.sendStructure(ctx, FileUtil.getFolder(userName));
    }

    @Override
    public void onDeletedFile(ChannelHandlerContext ctx, String filePath) {
        log.debug("Delete file {}", filePath);
        FileUtil.deleteFile(filePath);
    }

    @Override
    public void onReceivedFolder(ChannelHandlerContext ctx, String folder, String login) {
        try {
            CloudFolder cloudFolder = new ObjectMapper().readValue(folder, CloudFolder.class);
            FileUtil.saveFolder(cloudFolder, login, connectionUtil, ctx);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestFolder(ChannelHandlerContext ctx, String folderPath) {

    }

    @Override
    public void onReceivedFileForFolder(ChannelHandlerContext ctx, FilePacket filePacket) {
        try {
            File file = FileUtil.getFile(filePacket.getFilePath());
            writeToFile(file, filePacket.getFileSize(), filePacket.getFileContent(), filePacket.isFirst());
        } catch (IOException e) {
            log.warn("Error receiving file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestFileForFolder(ChannelHandlerContext ctx, String paths) {
        try {
            String[] path = paths.split(":sep:");
            String filePathClient = path[0];
            String filePathServer = path[1];
            File file = FileUtil.getFile(filePathClient);
            connectionUtil.sendFileForFolder(ctx, file, filePathServer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSignUpAttempt(ChannelHandlerContext ctx, CommandPacket commandPacket) {
        var login = commandPacket.getUsername();
        if (userSecureDAO.isCorrectUserName(login)) {
            userSecureDAO.createUserSecure(login, commandPacket.getBody());
            userDAO.saveUser(new User(login));
            onAuthSuccess(ctx, login);
        } else {
            onAuthFailed(ctx, "Username exist");
        }
    }

    @Override
    public void onCreateFolder(ChannelHandlerContext ctx, CommandPacket commandPacket) {
        FileUtil.createFolder(commandPacket.getBody());
    }

    private void writeToFile(File file, long size, byte[] bytes, boolean isFirst) throws IOException {
        try (FileOutputStream writer = new FileOutputStream(file, !isFirst)) {
            writer.write(bytes);
        }
    }

    public boolean checkToken(String userName, String token) {
        return tokens.get(userName).equals(token);
    }
}
