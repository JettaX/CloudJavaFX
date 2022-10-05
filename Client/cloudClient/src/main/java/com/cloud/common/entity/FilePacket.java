package com.cloud.common.entity;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class FilePacket implements Serializable {
    String fileName;
    String filePath;
    String newFileName;
    String newFilePath;
    long fileSize;
}
