package com.cloud.cloudclient.entity;

import javafx.scene.input.TransferMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferFile {
    String name;
    String path;
    TransferMode mode;
    boolean isLocal;
    boolean isDirectory;
}
