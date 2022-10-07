package com.cloud.cloudclient.utils;

import com.cloud.cloudclient.entity.TransferFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Clipboard {
    private static TransferFile file;

    public static void putFile(TransferFile transferFile) {
        file = transferFile;
    }

    public static TransferFile getFile() {
        return file;
    }

    public static boolean isEmpty() {
        return file == null;
    }
}
