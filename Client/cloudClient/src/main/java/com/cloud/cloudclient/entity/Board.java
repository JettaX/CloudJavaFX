package com.cloud.cloudclient.entity;

import javafx.scene.layout.VBox;
import lombok.Data;

@Data
public class Board implements Comparable<Board>{
    String name;
    long bytes;
    VBox board;

    public Board(String name, long bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    @Override
    public int compareTo(Board o) {
        return Long.compare(o.bytes, bytes);
    }
}
