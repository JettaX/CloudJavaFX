package com.cloud.cloud_server.util;


import com.cloud.common.entity.CloudFile;
import com.cloud.common.entity.CloudFolder;
import com.cloud.cloud_server.network.ConnectionUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    private static final String STORAGE_ROOT_PATH = System.getProperty("user.home")
            .concat("/FileCloud/FileServer");

    public static String getUserPath(String userName) {
        File userPath = new File(STORAGE_ROOT_PATH, userName);
        if (!userPath.exists()) {
            userPath.mkdirs();
        }
        return userPath.getAbsolutePath();
    }

    public static void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static File saveFile(String fileName, String username) throws IOException {
        File file = new File(getUserPath(username), fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static void saveFileByPath(String fileName, String path) throws IOException {
        File file = new File(path, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static File saveFileByPath(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static File getFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        throw new FileNotFoundException("File not found");
    }

    public static void saveFolder(CloudFolder cloudFolder, String username, ConnectionUtil connectionUtil, ChannelHandlerContext ctx) throws IOException {
        File file = new File(getUserPath(username));
        saveFolderIteration(cloudFolder, file, connectionUtil, ctx);
    }

    private static void saveFolderIteration(CloudFolder cloudFolder, File file, ConnectionUtil connectionUtil, ChannelHandlerContext ctx) {
        if (file.isDirectory()) {
            try {
                File newFile = new File(file, cloudFolder.getName());
                newFile.mkdir();
                if (cloudFolder.getCloudFolders() != null) {

                    for (CloudFolder childCloudFolder : cloudFolder.getCloudFolders()) {
                        saveFolderIteration(childCloudFolder, newFile, connectionUtil, ctx);
                    }
                }
                if (cloudFolder.getCloudFiles() != null) {
                    for (CloudFile childCloudFile : cloudFolder.getCloudFiles()) {
                        saveFileByPath(childCloudFile.getName(), newFile.getAbsolutePath());
                        File file1 = new File(newFile, childCloudFile.getName());
                        connectionUtil.requestFileForFolder(ctx, file1.getPath(), childCloudFile.getPath());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CloudFolder getFolder(String username) {
        CloudFolder rootCloudFolder = new CloudFolder("root", getUserPath(username), null);
        File files = new File(getUserPath(username));
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
        if (file.isFile()) {
            file.delete();
        } else {
            deleteFolder(path);
        }
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
