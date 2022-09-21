package com.cloud.cloudclient.utils;


import com.cloud.common.entity.CloudFile;
import com.cloud.common.entity.CloudFolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilesChecker {
    private static List<CloudFile> listLocalFiles = new ArrayList<>();
    private static List<CloudFile> listServerFiles = new ArrayList<>();

    public static List<CloudFile> getListLocalFiles() {
        return listLocalFiles;
    }

    public static List<CloudFile> getListServerFiles() {
        return listServerFiles;
    }

    public static boolean isLocalSave(CloudFile file) {
        return listLocalFiles.contains(file);
    }

    public static boolean isServerSave(CloudFile file) {
        return listServerFiles.contains(file);
    }

    public static void addLocalFile(CloudFile file) {
        listLocalFiles.add(file);
    }

    public static void addServerFile(CloudFile file) {
        listServerFiles.add(file);
    }

    public static void refillServerFiles(CloudFolder cloudFolder) {
        listServerFiles = new ArrayList<>();
        fillFiles(cloudFolder, listServerFiles);
    }

    public static void refillLocalFiles(CloudFolder cloudFolder) {
        listLocalFiles = new ArrayList<>();
        fillFiles(cloudFolder, listLocalFiles);
    }

    private static void fillFiles(CloudFolder cloudFolder, List<CloudFile> list) {
        list.addAll(cloudFolder.getCloudFiles());
        for (CloudFolder cloudFolder1 : cloudFolder.getCloudFolders()) {
            fillFiles(cloudFolder1, list);
        }
    }
}
