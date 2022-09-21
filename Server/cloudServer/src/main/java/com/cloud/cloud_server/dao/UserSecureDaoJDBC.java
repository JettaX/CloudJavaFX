package com.cloud.cloud_server.dao;

import com.cloud.cloud_server.entity.UserSecure;
import com.cloud.cloud_server.util.JdbcConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.SQLException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserSecureDaoJDBC implements UserSecureDAO {

    @Getter
    private static final UserSecureDaoJDBC INSTANCE = new UserSecureDaoJDBC();
    private static final String GET_BY_USERNAME_AND_PASSWORD =
            "SELECT * FROM user_secure WHERE user_login = ? AND user_password = ?";
    private static final String SAVE =
            "INSERT INTO user_secure (user_login, user_password) VALUES (?, ?)";
    private static final String UPDATE_LOGIN =
            "UPDATE user_secure SET user_login = ? WHERE user_login = ?";
    private static final String GET_BY_USERNAME =
            "SELECT * FROM user_secure WHERE user_login = ?";

    @Override
    public void createUserSecure(String login, String password) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(SAVE)) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUserSecure(UserSecure userSecure) {
        createUserSecure(userSecure.getUserLogin(), userSecure.getUserPassword());
    }

    @Override
    public boolean isCorrectAuth(String login, String password) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_BY_USERNAME_AND_PASSWORD)) {
            statement.setString(1, login);
            statement.setString(2, password);
            var resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateLogin(String login, String newLogin) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(UPDATE_LOGIN)) {
            statement.setString(1, newLogin);
            statement.setString(2, login);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCorrectUserName(String login) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_BY_USERNAME)) {
            statement.setString(1, login);
            var resultSet = statement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
