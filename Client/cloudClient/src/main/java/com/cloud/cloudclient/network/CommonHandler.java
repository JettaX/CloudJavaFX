package com.cloud.cloudclient.network;


import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.ServerCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        log.debug("channelRead0");
        if (msg instanceof CommandPacket commandPacket) {
            ServerCommand command = commandPacket.getCommand();
            log.debug(command.getCommand());

            switch (command) {
                case AUTHENTICATE_SUCCESS ->
                        eventListener.onAuthSuccess(commandPacket.getBody());
                case TOKEN_UPDATE ->
                        eventListener.onTokenUpdate(commandPacket.getToken());
                case AUTHENTICATE_ATTEMPT ->
                        eventListener.onAttemptAuth(commandPacket.getBody());
                case AUTHENTICATE_FAILED ->
                        eventListener.onAuthFailed(commandPacket.getBody());
                case STRUCTURE, REQUEST_STRUCTURE ->
                        eventListener.onReceiveStructure(commandPacket.getBody());
                case REQUEST_FILE ->
                        eventListener.onRequestFile(commandPacket.getBody());
                case RECEIVE_FILE, RECEIVE_FILE_FOR_FOLDER ->
                        beforeDownload(commandPacket, ctx);
                case REQUEST_FILE_FOR_FOLDER ->
                        eventListener.onRequestFileForFolder(commandPacket.getBody());
                default ->
                        log.debug("Unknown command: " + command);
            }

        } else if (msg instanceof ByteBuf buf) {
            ServerCommand command = waitingCommand.getCommand();
            switch (command) {
                case RECEIVE_FILE ->
                        eventListener.onReceiveFile(waitingFile.getFileName(), waitingFile.getFileSize(), buf);
                case RECEIVE_FILE_FOR_FOLDER ->
                        eventListener.onReceivedFileForFolder(waitingFile.getFilePath(), waitingFile.getFileSize(), buf);
                default ->
                        log.debug("Unknown command: " + command);
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
