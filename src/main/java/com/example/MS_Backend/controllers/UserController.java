package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.UserService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public String addUser(@RequestBody User user, HttpServletResponse response) {
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1800)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        return service.addUser(user);
    }

    @PostMapping("/userLogin")
    public String login(@RequestBody User user, HttpServletResponse response) {
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1800)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        return service.login(user);
    }

    @PatchMapping("/updatename/{username}/{newUsername}")
    public void changeName(@PathVariable String username, @PathVariable String newUsername, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.updateUsername(username, newUsername);
        }
    }

    @PatchMapping("/updatepw/{username}/{newPassword}")
    public void changePassword(@PathVariable String username, @PathVariable String newPassword, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.updatePassword(username, newPassword);
        }
    }

    @DeleteMapping("/delete/{username}")
    public void removeUser(@PathVariable String username, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.deleteUser(username);
        }
    }

    public boolean verify(HttpServletRequest request, HttpServletResponse response, String username) {
        Cookie[] cookies = request.getCookies();
        boolean expired = false;

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                String token = cookie.getValue();
                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(jwtService.getKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
                    System.out.println("IAT: " + claims.getIssuedAt());
                    System.out.println("EXP" + claims.getExpiration());

                } catch(ExpiredJwtException e) {
                    System.out.println("Token expired");
                    expired = true;
                } catch(JwtException e) {
                    System.out.println("This token is not valid");
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("Cookie expired or removed");
            return false;
        }

        if(expired) {
            String token = jwtService.generateToken(username);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(1800)
                    .sameSite("None")
                    .build();
            response.setHeader("Set-Cookie", cookie.toString());
        }
        return true;
    }
}
