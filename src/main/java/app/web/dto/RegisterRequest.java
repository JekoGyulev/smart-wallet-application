package app.web.dto;

import app.user.enums.Country;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotNull
    private Country country;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, Country country) {
        this.username = username;
        this.password = password;
        this.country = country;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
