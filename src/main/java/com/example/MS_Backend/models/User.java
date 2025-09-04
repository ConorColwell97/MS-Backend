package com.example.MS_Backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private List<Map<String, String>> movies = new ArrayList<>();

    public User() {}

    public User(String id, String username, String password, List<Map<String, String>> movies) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.movies = movies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Map<String, String>> getMovies() {
        return movies;
    }

    public void setMovies(List<Map<String, String>> movies) {
        this.movies = movies;
    }
}
