package com.cloud.cloudclient.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface TCPConnectionListener {
    void onConnected(String login);

    void onDisconnect(String login);

    void onException(Exception e);

    void onRequestFile(String fileName);

    void onReceiveFile(String fileName, long size, ByteBuf buf);

    void onReceiveStructure(String structure);

    void onAttemptAuth(String message) throws IOException;

    void onAuthSuccess(String login);

    void onAuthFailed(String message);

    void onTokenUpdate(String token);

    void onRequestFileForFolder(String paths);

    void onReceivedFileForFolder(String path, long size, ByteBuf buf);
}
