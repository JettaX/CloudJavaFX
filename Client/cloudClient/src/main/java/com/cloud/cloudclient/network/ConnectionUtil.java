package com.cloud.cloudclient.network;

import io.netty.channel.ChannelFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtil {
    private static TCPConnection connection = null;
    @Setter
    private static ChannelFuture DownloadConnection = null;
    private static BlockingQueue<Runnable> queue = new SynchronousQueue<>();
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, queue);

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
        executorService.submit(ClientNetty::getNewChannel);
        int count = 10;
        while ((DownloadConnection == null || !DownloadConnection.channel().isActive()) && count-- > 0) {
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
    }

    public static void remove() {
        connection.disconnect();
        connection = null;
    }

    public static boolean isConnected() {
        return connection != null;
    }
}
