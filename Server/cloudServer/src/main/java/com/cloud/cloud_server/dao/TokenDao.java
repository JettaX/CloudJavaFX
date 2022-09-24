package com.cloud.cloud_server.dao;

public interface TokenDao {

    void set(String username, String token);

    String get(String username);
}
