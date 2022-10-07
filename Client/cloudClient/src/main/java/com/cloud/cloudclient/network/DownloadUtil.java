package com.cloud.cloudclient.network;


import com.cloud.cloudclient.view.Indicators;
import com.cloud.cloudclient.view.TypeOfLoad;
import com.cloud.common.entity.CommandPacket;
import com.cloud.common.entity.FilePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.handler.stream.ChunkedStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadUtil {

    public static void downloadFile(File file, long size, ByteBuf buf) throws IOException {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file, true))) {
            int readable = buf.readableBytes();
            byte[] bytes = new byte[readable];
            buf.getBytes(buf.readerIndex(), bytes);
            writer.write(bytes);
            Indicators.downloading(file.getName(), size, bytes.length, TypeOfLoad.DOWNLOAD);
        }
    }

    public static void uploadFile(CommandPacket commandPacket, FilePacket filePacket, File file) throws IOException {
        String name = filePacket.getFileName();
        commandPacket.setObject(filePacket);
        ChannelFuture future = ConnectionUtil.getNewChannel();
        future.addListener(f -> {
            future.channel().writeAndFlush(commandPacket);
            future.channel().writeAndFlush(new ChunkedStream(new FileInputStream(file), 2048)).addListener(ff ->
                    ConnectionUtil.closeDownloadConnection());
                    ConnectionUtil.get().requestStructure();
            /*Indicators.downloading(name, total, (int) progress, TypeOfLoad.UPLOAD);*/
        });
    }
}
