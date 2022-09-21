package com.cloud.cloud_server.dao;


import com.cloud.cloud_server.entity.UserSecure;

public interface UserSecureDAO {

    public void createUserSecure(String login, String password);

    public void createUserSecure(UserSecure userSecure);

    public boolean isCorrectAuth(String login, String password);

    public boolean updateLogin(String login, String newLogin);

    public boolean isCorrectUserName(String login);
}
