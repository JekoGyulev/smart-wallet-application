package app.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests( matcher -> matcher
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // Static resources
                        .requestMatchers("/", "/register").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Authenticated endpoints
                )
                .formLogin( form -> form
                        .loginPage("/login") // Custom login endpoint
                        .defaultSuccessUrl("/home", true)  // After successful login -> redirect to /home always
                        .failureUrl("/login?error") // If login fails, then redirect to endpoint /login?error
                        .permitAll() // Everyone is allowed to access the custom /login endpoint
                )
                .logout( logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // Enable GET logout
                        .logoutSuccessUrl("/") // Redirect to index endpoint after logout
                );


        return httpSecurity.build();
    }




}
