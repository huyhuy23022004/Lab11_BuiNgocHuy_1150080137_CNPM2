package com.lab9.bai4;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class POJO map tới cấu trúc file users.json
 */
public class UserData {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    @JsonProperty("expectSuccess")
    private boolean expectSuccess;

    @JsonProperty("description")
    private String description;

    // --- Getter & Setter ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isExpectSuccess() {
        return expectSuccess;
    }

    public void setExpectSuccess(boolean expectSuccess) {
        this.expectSuccess = expectSuccess;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "user='" + username + '\'' +
                ", role='" + role + '\'' +
                ", success=" + expectSuccess +
                '}';
    }
}
