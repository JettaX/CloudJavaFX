package com.cloud.common.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode()
public class CloudFile {
    private String name;
    @EqualsAndHashCode.Exclude
    private String path;
    private long size;
    /*private String time;
    private String type;*/
}
