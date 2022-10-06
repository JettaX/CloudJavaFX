package com.cloud.cloudclient.network;

import io.netty.channel.ChannelFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtil {
    private static TCPConnection connection = null;
    @Setter
    private static ChannelFuture DownloadConnection = null;
    private static Thread downloadThread = null;

    public static void addIfNotExists(TCPConnectionListener listener) throws IOException {
        if (connection == null) {
            log.debug("Connection was add");
            connection = new TCPConnection(listener);
        }
    }

    public static TCPConnection get() {
        return connection;
    }

    public static ChannelFuture getNewChannel() {
        downloadThread = new Thread(() -> {
            try {
                ClientNetty.getNewChannel();
            } catch (Exception e) {
                log.debug("Thread interrupted");
            }
        });
        downloadThread.start();
        int count = 10;
        while (DownloadConnection == null && count-- > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return DownloadConnection;
    }

    public static void closeDownloadConnection() {
        DownloadConnection.channel().disconnect();
        DownloadConnection.channel().close();
        DownloadConnection = null;
        downloadThread.interrupt();
        downloadThread = null;
    }

    public static void remove() {
        connection.disconnect();
        connection = null;
    }

    public static boolean isConnected() {
        return connection != null;
    }
}
