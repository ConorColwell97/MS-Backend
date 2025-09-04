package com.example.MS_Backend.repository;

import com.example.MS_Backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface MovieRepo extends MongoRepository<User, String> {
    @Query(value = "{ username: ?0 }", fields = "{ movies: 1, _id: 0 }")
    Optional<String> findByUsername(String Username);
}
