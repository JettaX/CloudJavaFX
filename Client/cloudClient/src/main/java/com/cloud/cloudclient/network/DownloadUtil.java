package com.cloud.cloudclient.network;


import com.cloud.cloudclient.view.TypeOfLoad;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import com.cloud.cloudclient.view.Indicators;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadUtil {

    public static void downloadFile(File file, long size, boolean isFirst, byte[] bytes) throws IOException {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file, !isFirst), 1000000)) {
            writer.write(bytes);
            Indicators.downloading(file.getName(), size, bytes.length, TypeOfLoad.DOWNLOAD);
        }
    }

    public static void uploadFile(CommandPacket commandPacket, FilePacket filePacket, File file, Channel channel) throws IOException {
        var lengthReadBytes = 50000;
        var size = Files.size(file.toPath());
        var fileName = file.getName();

        filePacket.setFirst(true);
        filePacket.setLast(false);

        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file))) {
            while (reader.available() > 0) {
                checkAvailable(channel, 0);
                filePacket.setFileContent(reader.readNBytes(lengthReadBytes));
                commandPacket.setObject(filePacket);
                channel.writeAndFlush(commandPacket);
                Indicators.downloading(fileName, size, lengthReadBytes, TypeOfLoad.UPLOAD);
                filePacket.setFirst(false);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            log.debug("Fail while upload file");
        }
    }

    private static void checkAvailable(Channel channel, int count) throws InterruptedException {
        while (!channel.isWritable()) {
            if (count > 30) {
                throw new RuntimeException();
            }
            Thread.sleep(50);
            count++;
        }
    }
}
