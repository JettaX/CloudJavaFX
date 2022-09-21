package com.cloud.cloud_server.entity;

import lombok.*;

import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserSecure {
    private Long id;
    private String userLogin;
    private String userPassword;

    public UserSecure(String userLogin, String userPassword) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSecure that = (UserSecure) o;
        return Objects.equals(userLogin, that.userLogin) && Objects.equals(userPassword, that.userPassword);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
