package com.cloud.cloud_server.network;


import com.cloud.common.entity.CloudFolder;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.ServerCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedStream;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class ConnectionUtil {
    public void sendStructure(ChannelHandlerContext ctx, CloudFolder cloudFolder) {
        try {
            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.STRUCTURE)
                    .body(new ObjectMapper().writeValueAsString(cloudFolder))
                    .build();
            log.debug("Send structure: {}", cloudFolder.getName());
            ctx.writeAndFlush(commandPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToClient(ChannelHandlerContext ctx, CommandPacket commandPacket) {
        ctx.writeAndFlush(commandPacket);
    }

    public void writeToClient(ChannelHandlerContext ctx, ServerCommand command) {
        CommandPacket commandPacket = CommandPacket.builder()
                .command(command)
                .build();
        ctx.writeAndFlush(commandPacket);
    }

    public void sendFile(ChannelHandlerContext ctx, File file) {
        try {
            log.debug("Send file: {}", file.getName());
            // send command to client

            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.RECEIVE_FILE)
                    .build();

            FilePacket filePacket = FilePacket.builder()
                    .fileName(file.getName())
                    .fileSize(Files.size(file.toPath()))
                    .build();

            sendFile(file, commandPacket, filePacket, ctx);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestFileForFolder(ChannelHandlerContext ctx, String filePathServer,
                                     String filePathClient) {
        CommandPacket commandPacket = CommandPacket.builder()
                .command(ServerCommand.REQUEST_FILE_FOR_FOLDER)
                .body(filePathClient.concat(":sep:").concat(filePathServer))
                .build();
        ctx.writeAndFlush(commandPacket);
    }

    public void sendFileForFolder(ChannelHandlerContext ctx, File file, String filePathClient) {
        try {
            log.debug("Send file: {}", file.getName());

            CommandPacket commandPacket = CommandPacket.builder()
                    .command(ServerCommand.RECEIVE_FILE_FOR_FOLDER)
                    .build();

            FilePacket filePacket = FilePacket.builder()
                    .filePath(filePathClient)
                    .fileSize(Files.size(file.toPath()))
                    .build();

            sendFile(file, commandPacket, filePacket, ctx);
        } catch (IOException e) {
            log.error("Error sending file: {}", filePathClient, e);
        }
    }

    private void sendFile(File file, CommandPacket commandPacket, FilePacket filePacket, ChannelHandlerContext ctx) throws IOException {
        commandPacket.setObject(filePacket);
        ctx.writeAndFlush(commandPacket);
        ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file), 2048)).addListener(future ->
                ctx.channel().close());
    }
}
