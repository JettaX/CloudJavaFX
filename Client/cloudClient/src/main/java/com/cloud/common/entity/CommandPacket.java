package com.cloud.common.entity;


import com.cloud.common.util.ServerCommand;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommandPacket implements Serializable {
    private ServerCommand command;
    private String username;
    private String token;
    private String body;
    private Object object;
}
