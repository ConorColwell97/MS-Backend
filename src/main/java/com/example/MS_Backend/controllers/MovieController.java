package com.example.MS_Backend.controllers;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.services.MovieService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MovieController {

    @Autowired
    private MovieService service;

    @GetMapping("/genres")
    public JsonNode findGenres() throws Exception {
        return service.getGenres();
    }

    @GetMapping("/mymovies/{filters}")
    public List<Map<String, String>> getMovies(@PathVariable String filters) throws Exception {
        return service.searchMovies(filters);
    }

    @PutMapping("/addmovies/{username}")
    public void addMovies(@PathVariable String username, @RequestBody List<Map<String, String>> movies) {
        service.addMovies(username, movies);
    }

    @GetMapping("/getmovies/{username}")
    public JsonNode getMoviesByUser(@PathVariable String username, HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            System.out.println("This cookie is: " + cookie.getName() + ": " + cookie.getValue());
        }
        return service.getMovies(username);
    }

    @PatchMapping("/deletemovie/{username}/{title}")
    public void removeMovie(@PathVariable String username, @PathVariable String title) {
        service.deleteMovies(username, title);
    }

}
