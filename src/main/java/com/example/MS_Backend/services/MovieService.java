package com.example.MS_Backend.services;

import com.example.MS_Backend.repository.UserRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class MovieService {

    @Autowired
    private UserRepo repo;

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MovieService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode getGenres() throws Exception {
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key="+ apiKey + "&language=en-US";
        String response = restTemplate.getForObject(url, String.class);

        JsonNode json = objectMapper.readTree(response);
        return json.get("genres");
    }

    public List<Map<String, JsonNode>> searchMovies(String params) throws Exception {
        List <String> filters = Arrays.asList(params.split("&"));
        String url = "http://api.themoviedb.org/3/discover/movie?api_key=" + apiKey;

        for(String filter : filters) {
            url += ("&" + filter);
        }

        Random rand = new Random();
        int page = rand.nextInt(100) + 1;
        url += ("&page=" + page);

        String response = restTemplate.getForObject(url, String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode results = jsonNode.get("results");

        if(results.isEmpty()) {
            System.out.println(HttpStatus.NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found");
        }

        List<Map<String, JsonNode>> movies = new ArrayList<>();

        for(JsonNode json : results) {
            JsonNode title = json.get("title");
            JsonNode genres = json.get("genre_ids");
            JsonNode releaseDate = json.get("release_date");
            JsonNode overview = json.get("overview");

            Map<String, JsonNode> movie = new HashMap<>();
            movie.put("title", title);
            movie.put("genres", genres);
            movie.put("releaseDate", releaseDate);
            movie.put("overview", overview);

            movies.add(movie);
        }

        return movies;
    }
}
