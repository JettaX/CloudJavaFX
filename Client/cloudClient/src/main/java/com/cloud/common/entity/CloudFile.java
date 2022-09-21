package com.cloud.common.entity;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CloudFile {
    private String name;
    @EqualsAndHashCode.Exclude
    private String path;
    private long size;
    /*private String time;
    private String type;*/

    public CloudFile(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }
}
