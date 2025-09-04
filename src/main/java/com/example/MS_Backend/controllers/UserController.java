package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public String addUser(@RequestBody User user) {
        return service.addUser(user);
    }

    @PostMapping("/userLogin")
    public String login(@RequestBody User user) {
        return service.login(user);
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
