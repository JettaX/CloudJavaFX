package com.cloud.cloudclient.entity;

import lombok.Data;

import java.util.List;

@Data
public class DashBoard {
    Board image;
    Board document;
    Board video;
    Board music;
    Board other;

    public DashBoard() {
        image = new Board("Images", 0);
        document = new Board("Documents", 0);
        video = new Board("Videos", 0);
        music = new Board("Music", 0);
        other = new Board("Other", 0);
    }

    public void addImage(long bytes) {
        image.bytes += bytes;
    }

    public void addDocument(long bytes) {
        document.bytes += bytes;
    }

    public void addVideo(long bytes) {
        video.bytes += bytes;
    }

    public void addMusic(long bytes) {
        music.bytes += bytes;
    }

    public void addOther(long bytes) {
        other.bytes += bytes;
    }

    public List<Board> getAll() {
        return List.of(image, document, video, music, other);
    }
}
