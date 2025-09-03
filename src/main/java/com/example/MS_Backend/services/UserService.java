package com.example.MS_Backend.services;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.repository.UserRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String addUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            repo.save(user);
            return jwtService.generateToken(user.getUsername());
        } catch(MongoWriteException e) {
            if (e.getError().getCategory() == com.mongodb.ErrorCategory.DUPLICATE_KEY) {
                throw new RuntimeException("Username '"+user.getUsername()+"' already exists");
            }
            throw e;
        }
    }

    public String login(User user) {
        User currUser = repo.findByUsername(user.getUsername()).orElse(null);

        if(currUser == null) {
            throw new RuntimeException("User not found");
        }

        boolean matches = encoder.matches(user.getPassword(), currUser.getPassword());

        if(matches) {
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new RuntimeException("Incorrect password");
        }
    }
}
