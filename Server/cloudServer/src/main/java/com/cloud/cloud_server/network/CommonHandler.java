package com.cloud.cloud_server.network;


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

    private TCPListener eventListener;
    private CommandPacket waitingCommand;
    private FilePacket waitingFile;

    public CommonHandler(TCPListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelUnregistered");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("ChanelRead");
        if (msg instanceof CommandPacket commandPacket) {
            String command = commandPacket.getCommand().getCommand();
            if (command.equals(ServerCommand.AUTH_WITH_PASSWORD.getCommand())) {
                log.debug(command);
                eventListener.onAttemptAuthWithLoginPassword(ctx, commandPacket.getUsername(), commandPacket.getBody());
            } else if (command.equals(ServerCommand.AUTH_WITH_TOKEN.getCommand())) {
                log.debug(command);
                eventListener.onAttemptAuthWithToken(ctx, commandPacket);
            } else if (command.equals(ServerCommand.AUTH_SIGN_UP.getCommand())) {
                log.debug(command);
                eventListener.onSignUpAttempt(ctx, commandPacket);
            } else if (!eventListener.isTokenValid(commandPacket.getUsername(), commandPacket.getToken())) {
                log.debug("Token is invalid");
                eventListener.onDisconnect(ctx, commandPacket.getUsername());
            } else if (command.equals(ServerCommand.REQUEST_STRUCTURE.getCommand())) {
                log.debug(command);
                eventListener.onRequestStructure(ctx, commandPacket.getUsername());
            } else if (command.equals(ServerCommand.REQUEST_FILE.getCommand())) {
                log.debug(command);
                eventListener.onRequestFile(ctx, commandPacket.getBody());
            } else if (command.equals(ServerCommand.RECEIVE_FILE.getCommand()) ||
                    command.equals(ServerCommand.RECEIVE_FILE_FOR_FOLDER.getCommand())) {
                log.debug(command);
                beforeDownload(commandPacket, ctx);
            } else if (command.equals(ServerCommand.DELETE_FILE.getCommand())) {
                log.debug(command);
                eventListener.onDeletedFile(ctx, commandPacket.getBody());
                eventListener.onRequestStructure(ctx, commandPacket.getUsername());
            } else if (command.equals(ServerCommand.RECEIVE_FOLDER.getCommand())) {
                log.debug(command);
                eventListener.onReceivedFolder(ctx, commandPacket.getBody(), commandPacket.getUsername());
            } else if (command.equals(ServerCommand.REQUEST_FILE_FOR_FOLDER.getCommand())) {
                log.debug(command);
                String filePath = commandPacket.getBody();
                eventListener.onRequestFileForFolder(ctx, filePath);
            } else if (command.equals(ServerCommand.CREATE_FOLDER.getCommand())) {
                log.debug(command);
                eventListener.onCreateFolder(ctx, commandPacket);
            } else if (command.equals(ServerCommand.RENAME_FILE.getCommand())) {
                log.debug(command);
                eventListener.onRenameFile(ctx, commandPacket);
            }

        } else if (msg instanceof ByteBuf buf) {
            String command = waitingCommand.getCommand().getCommand();
            if (command.equals(ServerCommand.RECEIVE_FILE.getCommand())) {
                eventListener.onReceivingFile(ctx, waitingCommand, waitingFile, buf);
            } else if (command.equals(ServerCommand.RECEIVE_FILE_FOR_FOLDER.getCommand())) {
                eventListener.onReceivedFileForFolder(ctx, waitingFile, buf);
            }
        }
    }

    private void beforeDownload(CommandPacket commandPacket, ChannelHandlerContext ctx) {
        waitingFile = (FilePacket) commandPacket.getObject();
        waitingCommand = commandPacket;
        ctx.channel().pipeline().remove("decoder");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug(cause.getMessage());
        /*ctx.close();*/
    }
}
