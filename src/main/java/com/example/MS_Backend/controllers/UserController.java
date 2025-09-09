package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
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

    @PatchMapping("/updatename/{username}")
    public void changeName(@PathVariable String username, @RequestBody JsonNode data, HttpServletRequest request, HttpServletResponse response) {
        String newUsername = data.get("newName").asText();
        if(verify(request, response, username)) {
            service.updateUsername(username, newUsername);
        }
    }

    @PatchMapping("/updatepw/{username}")
    public void changePassword(@PathVariable String username, @RequestBody JsonNode data, HttpServletRequest request, HttpServletResponse response) {
        String newPassword = data.get("newPW").asText();
        if(verify(request, response, username)) {
            service.updatePassword(username, newPassword);
        }
    }

    @DeleteMapping("/delete/{username}/{password}")
    public void removeUser(@PathVariable String username, @PathVariable String password, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.deleteUser(username, password);
            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();
            response.setHeader("Set-Cookie", cookie.toString());
        }
    }

    @DeleteMapping("/userLogout")
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
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

                } catch(ExpiredJwtException e) {
                    System.out.println("Token expired");
                    throw new RuntimeException(e);
                } catch(JwtException e) {
                    System.out.println("This token is not valid");
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("Cookie expired or removed");
            return false;
        }

//        if(expired) {
//            String token = jwtService.generateToken(username);
//
//            ResponseCookie cookie = ResponseCookie.from("token", token)
//                    .httpOnly(true)
//                    .secure(true)
//                    .path("/")
//                    .maxAge(1800)
//                    .sameSite("None")
//                    .build();
//            response.setHeader("Set-Cookie", cookie.toString());
//        }
        return true;
    }
}
