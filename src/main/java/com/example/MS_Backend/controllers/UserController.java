package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public String addUser(@RequestBody User user, HttpServletResponse response) {
        jwtService.generateToken(user.getUsername(), response);
        return service.addUser(user);
    }

    @PostMapping("/userLogin")
    public String login(@RequestBody User user, HttpServletResponse response) {
        jwtService.generateToken(user.getUsername(), response);
        return service.login(user);
    }

    @PatchMapping("/updatename/{username}")
    public void changeName(@PathVariable String username, @RequestBody JsonNode data, HttpServletRequest request, HttpServletResponse response) {
        String newUsername = data.get("newName").asText();

        if(jwtService.isValidToken(request, response, username)) {
            service.updateUsername(username, newUsername);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }

    }

    @PatchMapping("/updatepw/{username}")
    public void changePassword(@PathVariable String username, @RequestBody JsonNode data, HttpServletRequest request, HttpServletResponse response) {
        String newPassword = data.get("newPW").asText();

        if(jwtService.isValidToken(request, response, username)) {
            service.updatePassword(username, newPassword);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }

    @DeleteMapping("/delete/{username}/{password}")
    public void removeUser(@PathVariable String username, @PathVariable String password, HttpServletRequest request, HttpServletResponse response) {
        if(jwtService.isValidToken(request, response, username)) {
            service.deleteUser(username, password);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
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
}
