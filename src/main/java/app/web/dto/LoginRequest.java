package app.web.dto;

import jakarta.validation.constraints.Size;

public class LoginRequest {

    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

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
}
