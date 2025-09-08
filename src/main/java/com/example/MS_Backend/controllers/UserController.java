package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public void addUser(@RequestBody User user, HttpServletResponse response) {
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1800)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
        service.addUser(user);
    }

    @PostMapping("/userLogin")
    public void login(@RequestBody User user, HttpServletResponse response) {
        service.login(user);
        String token = jwtService.generateToken(user.getUsername());
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1800)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

    @PatchMapping("/updatename/{username}/{newUsername}")
    public void changeName(@PathVariable String username, @PathVariable String newUsername) {
        service.updateUsername(username, newUsername);
    }

    @PatchMapping("/updatepw/{username}/{newPassword}")
    public void changePassword(@PathVariable String username, @PathVariable String newPassword) {
        service.updatePassword(username, newPassword);
    }

    @DeleteMapping("/delete/{username}")
    public void removeUser(@PathVariable String username) {
        service.deleteUser(username);
    }
}
