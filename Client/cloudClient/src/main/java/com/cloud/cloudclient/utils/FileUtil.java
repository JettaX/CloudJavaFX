package com.cloud.cloudclient.utils;


import com.cloud.cloudclient.Main;
import com.cloud.common.entity.CloudFile;
import com.cloud.common.entity.CloudFolder;
import com.cloud.cloudclient.network.ConnectionUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    private static final String STORAGE_ROOT_PATH = System.getProperty("user.home")
            .concat("/FileCloud/FilesClient");

    public static String getUserPath() {
        File userPath = new File(STORAGE_ROOT_PATH, Main.user.getUsername());
        if (!userPath.exists()) {
            userPath.mkdirs();
        }
        return userPath.getAbsolutePath();
    }

    public static File saveFile(String fileName) throws IOException {
        File file = new File(getUserPath(), fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static void createFolder(String path, String folderName) {
        File file = new File(path, folderName);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void saveFileByPath(String fileName, String path) throws IOException {
        File file = new File(path, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void saveFolder(CloudFolder cloudFolder) {
        File file = new File(getUserPath());
        saveFolderIteration(cloudFolder, file);
    }

    public static void renameFile(String path, String oldName, String newName) {
        File file = new File(path);
        String newPath = path.split(oldName)[0];
        file.renameTo(new File(newPath, newName));
    }

    public static void moveFile(String fileName, String filePath, String folderPath) throws IOException {
        File file = new File(folderPath, fileName);
        Files.move(Path.of(filePath), Path.of(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFile(String fileName, String filePath, String folderPath) throws IOException {
        File file = new File(folderPath, fileName);
        Files.copy(Path.of(filePath), Path.of(file.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void saveFolderIteration(CloudFolder cloudFolder, File file) {
        if (file.isDirectory()) {
            try {
                File newFile = new File(file, cloudFolder.getName());
                newFile.mkdir();
                if (cloudFolder.getCloudFolders() != null) {

                    for (CloudFolder childCloudFolder : cloudFolder.getCloudFolders()) {
                        saveFolderIteration(childCloudFolder, newFile);
                    }
                }
                if (cloudFolder.getCloudFiles() != null) {
                    for (CloudFile childCloudFile : cloudFolder.getCloudFiles()) {
                        saveFileByPath(childCloudFile.getName(), newFile.getAbsolutePath());
                        File file1 = new File(newFile, childCloudFile.getName());
                        ConnectionUtil.get().requestFileForFolder(childCloudFile.getPath(), file1.getPath());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static File getFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        throw new FileNotFoundException("File not found");
    }


    public static CloudFolder getRootFolder() {
        CloudFolder rootCloudFolder = new CloudFolder("root", getUserPath(), null);
        File files = new File(getUserPath());
        return getFullHierarchy(rootCloudFolder, files);
    }

    private static CloudFolder getFullHierarchy(CloudFolder cloudFolder, File file) {
        File[] list = file.listFiles();
        try {
            for (File f : list) {
                if (f.isDirectory()) {
                    CloudFolder childCloudFolder = new CloudFolder(f.getName(), f.getAbsolutePath(), cloudFolder);
                    cloudFolder.addFolder(childCloudFolder);
                    getFullHierarchy(childCloudFolder, f);
                } else {
                    cloudFolder.addFile(new CloudFile(f.getName(), f.getAbsolutePath(), Files.size(f.toPath())));
                }
            }
        } catch (IOException e) {
            log.warn("Something wrong");
        }
        return cloudFolder;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    public static void deleteFolder(String path) {
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                deleteFolder(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
        file.delete();
    }
}
