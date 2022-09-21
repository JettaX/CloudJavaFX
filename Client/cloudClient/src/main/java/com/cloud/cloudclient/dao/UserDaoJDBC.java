package com.cloud.cloudclient.dao;


import com.cloud.cloudclient.entity.User;
import com.cloud.cloudclient.utils.JdbcConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserDaoJDBC implements UserDAO {
    @Getter
    private static final UserDaoJDBC INSTANCE = new UserDaoJDBC();
    private static final String SAVE_USER = "INSERT INTO users_cloud (" +
            "username, image_url) VALUES (?, ?)";
    private static final String UPDATE_USER = "UPDATE users_cloud SET " +
            "username = ?, image_url = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users_cloud WHERE username = ?";
    private static final String GET_ALL_USERS = "SELECT * FROM users_cloud";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM users_cloud WHERE username = ?";
    private static final String SEARCH_USER_BY_USERNAME = "SELECT * FROM users_cloud WHERE lower(username) LIKE ? ";
    private static final String GET_USER_BY_ID = "SELECT * FROM users_cloud WHERE id = ?";

    @Override
    public User saveUser(User user) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getImageUrl());
            statement.executeUpdate();
            var resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUser(User oldUser, User newUser) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setString(1, newUser.getUsername());
            statement.setString(2, newUser.getImageUrl());
            statement.setLong(3, oldUser.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByUserName(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            statement.setString(1, userName);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getLong("id"))
                        .username(resultSet.getString("username"))
                        .imageUrl(resultSet.getString("image_url"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User getUserByID(Long id) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_USER_BY_ID)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getLong("id"))
                        .username(resultSet.getString("username"))
                        .imageUrl(resultSet.getString("image_url"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<User> searchUser(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SEARCH_USER_BY_USERNAME)) {
            statement.setString(1, "%" + userName.toLowerCase() + "%");
            var resultSet = statement.executeQuery();
            var users = new ArrayList<User>();
            while (resultSet.next()) {
                users.add(User.builder()
                        .id(resultSet.getLong("id"))
                        .username(resultSet.getString("username"))
                        .imageUrl(resultSet.getString("image_url"))
                        .build());
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getUsers() {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_ALL_USERS)) {
            var resultSet = statement.executeQuery();
            var users = new ArrayList<User>();
            while (resultSet.next()) {
                users.add(
                        User.builder()
                                .id(resultSet.getLong("id"))
                                .username(resultSet.getString("username"))
                                .imageUrl(resultSet.getString("image_url"))
                                .build());
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserById(String userName) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(DELETE_USER)) {
            statement.setString(1, userName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
