package com.cloud.cloud_server.dao;


import com.cloud.cloud_server.entity.User;

import java.util.List;

public interface UserDAO {

    public User saveUser(User user);

    public void updateUser(User oldUser, User newUser);

    public User getUserByUserName(String userName);

    public User getUserByID(Long id);

    public List<User> searchUser(String userName);

    public List<User> getUsers();

    public void deleteUserById(String userName);

}
