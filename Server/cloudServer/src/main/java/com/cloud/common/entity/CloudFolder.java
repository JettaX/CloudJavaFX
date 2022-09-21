package com.cloud.common.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@folderId")
public class CloudFolder {
    private String name;
    private String path;
    private List<CloudFile> cloudFiles = new ArrayList<>();
    private CloudFolder parentCloudFolder = null;
    private List<CloudFolder> cloudFolders = new ArrayList<>();

    public CloudFolder(String name, String path, CloudFolder parentCloudFolder) {
        this.name = name;
        this.path = path;
        this.parentCloudFolder = parentCloudFolder;
    }

    public void addFile(CloudFile cloudFile) {
        cloudFiles.add(cloudFile);
    }

    public void addFolder(CloudFolder cloudFolder) {
        cloudFolders.add(cloudFolder);
    }
}
