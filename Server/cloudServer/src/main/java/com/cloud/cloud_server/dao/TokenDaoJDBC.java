package com.cloud.cloud_server.dao;

import com.cloud.cloud_server.util.JdbcConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.SQLException;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TokenDaoJDBC implements TokenDao {
    @Getter
    private static final TokenDaoJDBC INSTANCE = new TokenDaoJDBC();

    private static final String SET_TOKEN = "INSERT INTO tokens (" +
            "username, token) VALUES (?, ?)";

    private static final String UPDATE_TOKEN = "UPDATE tokens SET " +
            "token = ? WHERE username = ?";

    private static final String GET_TOKEN = "SELECT * FROM tokens WHERE username = ?";


    @Override
    public void set(String username, String token) {
        if (get(username) == null) {
            try (var connection = JdbcConnection.getConnection();
                 var statement = connection.prepareStatement(SET_TOKEN)) {
                statement.setString(1, username);
                statement.setString(2, token);
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (var connection = JdbcConnection.getConnection();
                 var statement = connection.prepareStatement(UPDATE_TOKEN)) {
                statement.setString(1, token);
                statement.setString(2, username);
                statement.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public String get(String username) {
        try (var connection = JdbcConnection.getConnection();
             var statement = connection.prepareStatement(GET_TOKEN)) {
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("token");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
