package com.cloud.common.util;

import java.io.Serializable;

public enum ServerCommand implements Serializable {
    AUTH_WITH_PASSWORD("/auth_with_password"),
    AUTH_WITH_TOKEN("/auth_with_token"),
    AUTH_SIGN_UP("/auth_sign_up"),
    AUTHENTICATE_FAILED("/auth_failed"),
    AUTHENTICATE_SUCCESS("/auth_success"),
    AUTHENTICATE_ATTEMPT("/attempts_over"),
    TOKEN_UPDATE("/token_update"),
    RECEIVE_FILE("/receive_file"),
    REQUEST_FILE("/request_file"),
    RECEIVE_FOLDER("/receive_folder"),
    REQUEST_FOLDER("/request_folder"),
    REQUEST_FILE_FOR_FOLDER("/request_file_for_folder"),
    RECEIVE_FILE_FOR_FOLDER("/receive_file_for_folder"),
    STRUCTURE("/structure"),
    REQUEST_STRUCTURE("/request_structure"),
    DELETE_FILE("/delete_file");
    private String command;

    ServerCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
