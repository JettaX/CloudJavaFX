package com.cloud.cloudclient.network;


import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.ServerCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@ChannelHandler.Sharable
public class CommonHandler extends SimpleChannelInboundHandler<Object> {

    TCPListener eventListener = TCPListener.getINSTANCE();
    private CommandPacket waitingCommand;
    private FilePacket waitingFile;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelUnregistered");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws IOException {
        log.debug("channelRead0");
        if (msg instanceof CommandPacket commandPacket) {
            String command = commandPacket.getCommand().getCommand();
            if (command.equals(ServerCommand.AUTHENTICATE_SUCCESS.getCommand())) {
                log.debug("AUTHENTICATE_SUCCESS");
                eventListener.onAuthSuccess(commandPacket.getBody());
            } else if (command.equals(ServerCommand.TOKEN_UPDATE.getCommand())) {
                log.debug("TOKEN_UPDATE");
                eventListener.onTokenUpdate(commandPacket.getToken());
            } else if (ServerCommand.AUTHENTICATE_ATTEMPT.getCommand().equals(command)) {
                log.debug("AUTHENTICATE_ATTEMPT");
                eventListener.onAttemptAuth(commandPacket.getBody());
            } else if (ServerCommand.AUTHENTICATE_FAILED.getCommand().equals(command)) {
                log.debug("AUTHENTICATE_FAILED");
                eventListener.onAuthFailed(commandPacket.getBody());
            } else if (command.equals(ServerCommand.STRUCTURE.getCommand()) ||
                    command.equals(ServerCommand.REQUEST_STRUCTURE.getCommand())) {
                eventListener.onReceiveStructure(commandPacket.getBody());
            } else if (command.equals(ServerCommand.REQUEST_FILE.getCommand())) {
                eventListener.onRequestFile(commandPacket.getBody());
            } else if (command.equals(ServerCommand.RECEIVE_FILE.getCommand()) ||
                    command.equals(ServerCommand.RECEIVE_FILE_FOR_FOLDER.getCommand())) {
                beforeDownload(commandPacket, ctx);
            } else if (command.equals(ServerCommand.REQUEST_FILE_FOR_FOLDER.getCommand())) {
                eventListener.onRequestFileForFolder(commandPacket.getBody());
            }

        } else if (msg instanceof ByteBuf buf) {
            String command = waitingCommand.getCommand().getCommand();
            if (command.equals(ServerCommand.RECEIVE_FILE.getCommand())) {
                eventListener.onReceiveFile(waitingFile.getFileName(), waitingFile.getFileSize(), buf);
            } else if (command.equals(ServerCommand.RECEIVE_FILE_FOR_FOLDER.getCommand())) {
                eventListener.onReceivedFileForFolder(waitingFile.getFilePath(), waitingFile.getFileSize(), buf);
            }
        }
    }

    private void beforeDownload(CommandPacket commandPacket, ChannelHandlerContext ctx) {
        waitingFile = (FilePacket) commandPacket.getObject();
        waitingCommand = commandPacket;
        ctx.channel().pipeline().remove("decoder");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug(cause.getMessage());
        /*ctx.close();*/
    }
}
