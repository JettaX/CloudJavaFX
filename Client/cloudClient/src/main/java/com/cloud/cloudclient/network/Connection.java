package com.cloud.cloudclient.network;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Connection {

    private static TCPConnection connection = null;

    public static void addIfNotExists(TCPConnectionListener listener) throws IOException {
        if (connection == null) {
            log.debug("Connection was add");
            connection = new TCPConnection(listener);
        }
    }

    public static TCPConnection get() {
        return connection;
    }

    public static void remove() {
        connection.disconnect();
        connection = null;
    }

    public static boolean isConnected() {
        return connection != null;
    }
}
