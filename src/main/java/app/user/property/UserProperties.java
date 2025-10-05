package app.user.property;

import app.user.enums.Country;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "users")
@Data
public class UserProperties {

    private DefaultUser defaultUser;

    private String testProperty;

    @Data
    public static class DefaultUser {
        private String username;
        private String password;
        private Country country;
    }


//    @PostConstruct
//    public void testMethod() {
//        System.out.println();
//    }

}
