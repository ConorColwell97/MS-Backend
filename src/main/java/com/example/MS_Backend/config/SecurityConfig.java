package com.example.MS_Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/register" ,"/userLogin", "/genres/{username}",
                                "/mymovies/{username}/{filters}", "/addmovies/{username}", "/getmovies/{username}",
                                "/deletemovies/{username}","/updatename/{username}",
                                "/updatepw/{username}", "/delete/{username}", "/userLogout", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(customizer -> customizer.disable())
                .httpBasic(customizer -> customizer.disable());

        return http.build();
    }
}
