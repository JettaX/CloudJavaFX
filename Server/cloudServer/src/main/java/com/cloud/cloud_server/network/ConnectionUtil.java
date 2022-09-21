package com.cloud.cloud_server.network;


import com.cloud.common.entity.CloudFolder;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.common.util.ServerCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
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
            System.out.println(e.getMessage());
        }
    }

    private void sendFile(File file, CommandPacket commandPacket, FilePacket filePacket, ChannelHandlerContext ctx) throws IOException {
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file))) {
            var lengthReadBytes = 50000;
            filePacket.setFirst(true);
            while (reader.available() > 0) {
                checkAvailable(ctx.channel());
                filePacket.setFileContent(reader.readNBytes(lengthReadBytes));
                commandPacket.setObject(filePacket);
                ctx.channel().writeAndFlush(commandPacket);
                filePacket.setFirst(false);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkAvailable(Channel channel) throws InterruptedException {
        int count = 0;
        while (!channel.isWritable()) {
            log.debug("Wait channel");
            if (count > 30) {
                throw new RuntimeException();
            }
            Thread.sleep(50);
            count++;
        }
    }
}
