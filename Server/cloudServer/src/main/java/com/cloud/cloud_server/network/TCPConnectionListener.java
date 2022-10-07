package com.cloud.cloud_server.network;

import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface TCPConnectionListener {
    void onConnected(ChannelHandlerContext ctx, String login);
    void onDisconnect(ChannelHandlerContext ctx, String login);
    void onException(ChannelHandlerContext ctx, Exception e);
    void onRequestFile(ChannelHandlerContext ctx, String fileName);
    void onAttemptAuthWithLoginPassword(ChannelHandlerContext ctx, String username, String password) throws IOException;
    void onAttemptAuthWithToken(ChannelHandlerContext ctx, CommandPacket commandPacket) throws IOException;
    void onAuthSuccess(ChannelHandlerContext ctx, String login);
    void onAuthFailed(ChannelHandlerContext ctx, String message);
    void onReceivingFile(ChannelHandlerContext ctx, CommandPacket commandPacket, FilePacket filePacket, ByteBuf buf);
    void onRequestStructure(ChannelHandlerContext ctx, String userName);
    void onDeletedFile(ChannelHandlerContext ctx, String filePath);
    void onReceivedFolder(ChannelHandlerContext ctx, String folder, String login);
    void onRequestFolder(ChannelHandlerContext ctx, String folderPath);
    void onReceivedFileForFolder(ChannelHandlerContext ctx, FilePacket filePacket, ByteBuf buf);
    void onRequestFileForFolder(ChannelHandlerContext ctx, String paths);
    void onSignUpAttempt(ChannelHandlerContext ctx, CommandPacket commandPacket);
    void onCreateFolder(ChannelHandlerContext ctx, CommandPacket commandPacket);
    void onRenameFile(ChannelHandlerContext ctx, CommandPacket commandPacket);
    void onCopyFile(ChannelHandlerContext ctx, FilePacket filePacket);
    void onMoveFile(ChannelHandlerContext ctx, FilePacket filePacket);
}
