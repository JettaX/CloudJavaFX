package com.cloud.cloudclient.view.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SizeUtil {

    public static String calculateSize(long sizeOfBytes) {
        double size = sizeOfBytes;
        String prefix = "bytes";
        if (sizeOfBytes >= 1000000) {
            size = Math.round(sizeOfBytes / 1000000D);
            prefix = "mb";
        } else if (sizeOfBytes >= 1000) {
            size = Math.round(sizeOfBytes / 1000D);
            prefix = "kb";
        }
        return String.valueOf(size).concat(" ").concat(prefix);
    }
}
