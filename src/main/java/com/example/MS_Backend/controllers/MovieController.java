package com.example.MS_Backend.controllers;

import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.MovieService;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class MovieController {

    @Autowired
    private MovieService service;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/mymovies/{username}/{filters}")
    public List<Map<String, String>> getMovies(@PathVariable String username, @PathVariable String filters, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(jwtService.isValidToken(request, response, username)) {
            return service.searchMovies(filters);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }

    @PutMapping("/addmovies/{username}")
    public void addMovies(@PathVariable String username, @RequestBody List<Map<String, String>> movies, HttpServletRequest request, HttpServletResponse response) {
        if(jwtService.isValidToken(request, response, username)) {
            service.addMovies(username, movies);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }

    @GetMapping("/getmovies/{username}")
    public JsonNode getMoviesByUser(@PathVariable String username, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(jwtService.isValidToken(request, response, username)) {
            return service.getMovies(username);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }

    @PatchMapping("/deletemovies/{username}")
    public void removeMovies(@PathVariable String username, @RequestBody List<String> titles, HttpServletRequest request, HttpServletResponse response) {
        if(jwtService.isValidToken(request, response, username)) {
            service.deleteMovies(username, titles);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized");
        }
    }
}