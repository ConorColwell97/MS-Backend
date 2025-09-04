package com.example.MS_Backend.services;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.repository.UserRepo;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MongoTemplate mongoTemplate;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        boolean matches = encoder.matches(user.getPassword(), currUser.getPassword());

        if(matches) {
            return jwtService.generateToken(user.getUsername());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect password");
        }
    }

    public void updateUsername(String username, String newUsername) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        Update update = new Update().set("username", newUsername);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public void updatePassword(String username, String newPassword) {
        String hashedPassword = encoder.encode(newPassword);
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        Update update = new Update().set("password", hashedPassword);
        mongoTemplate.updateFirst(query, update, User.class);
    }

    public void deleteUser(String username) {
        repo.deleteByUsername(username);
    }
}
