package com.example.MS_Backend.controllers;

import com.example.MS_Backend.services.MovieService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class MovieController {

    @Autowired
    private MovieService service;

    @GetMapping("/genres")
    public JsonNode findGenres() throws Exception {
        return service.getGenres();
    }

    @GetMapping("/mymovies/{filters}")
    public List<Map<String, JsonNode>> getMovies(@PathVariable String filters) throws Exception {
        return service.searchMovies(filters);
    }
}
