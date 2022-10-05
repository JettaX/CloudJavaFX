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

    private final TCPListener eventListener;
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
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        log.debug("ChanelRead");
        if (msg instanceof CommandPacket commandPacket) {
            ServerCommand command = commandPacket.getCommand();
            log.debug(command.getCommand());

            switch (command) {
                case AUTH_WITH_PASSWORD -> eventListener.onAttemptAuthWithLoginPassword(ctx,
                        commandPacket.getUsername(), commandPacket.getBody());
                case AUTH_WITH_TOKEN ->
                        eventListener.onAttemptAuthWithToken(ctx, commandPacket);
                case AUTH_SIGN_UP ->
                        eventListener.onSignUpAttempt(ctx, commandPacket);
                case REQUEST_STRUCTURE ->
                        eventListener.onRequestStructure(ctx, commandPacket.getUsername());
                case REQUEST_FILE ->
                        eventListener.onRequestFile(ctx, commandPacket.getBody());
                case RECEIVE_FILE, RECEIVE_FILE_FOR_FOLDER ->
                        beforeDownload(commandPacket, ctx);
                case DELETE_FILE -> {
                    eventListener.onDeletedFile(ctx, commandPacket.getBody());
                    eventListener.onRequestStructure(ctx, commandPacket.getUsername());
                }
                case RECEIVE_FOLDER ->
                        eventListener.onReceivedFolder(ctx, commandPacket.getBody(), commandPacket.getUsername());
                case REQUEST_FILE_FOR_FOLDER ->
                        eventListener.onRequestFileForFolder(ctx, commandPacket.getBody());
                case CREATE_FOLDER ->
                        eventListener.onCreateFolder(ctx, commandPacket);
                case RENAME_FILE ->
                        eventListener.onRenameFile(ctx, commandPacket);
                default ->
                        log.debug("Unknown command: " + command);
            }

        } else if (msg instanceof ByteBuf buf) {
            ServerCommand command = waitingCommand.getCommand();
            switch (command) {
                case RECEIVE_FILE ->
                        eventListener.onReceivingFile(ctx, waitingCommand, waitingFile, buf);
                case RECEIVE_FILE_FOR_FOLDER ->
                        eventListener.onReceivedFileForFolder(ctx, waitingFile, buf);
                default ->
                        log.debug("Unknown command: " + command);
            }
        }
    }

    private void beforeDownload(CommandPacket commandPacket, ChannelHandlerContext ctx) {
        checkToken(ctx, commandPacket);
        waitingFile = (FilePacket) commandPacket.getObject();
        waitingCommand = commandPacket;
        ctx.channel().pipeline().remove("decoder");
    }

    private void checkToken(ChannelHandlerContext ctx, CommandPacket commandPacket) {
        if (!eventListener.isTokenValid(commandPacket.getUsername(), commandPacket.getToken())) {
            log.debug("Token is invalid");
            eventListener.onDisconnect(ctx, commandPacket.getUsername());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug(cause.getMessage());
        /*ctx.close();*/
    }
}
