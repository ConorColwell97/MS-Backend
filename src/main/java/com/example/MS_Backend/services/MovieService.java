package com.example.MS_Backend.services;

import com.example.MS_Backend.models.User;
import com.example.MS_Backend.repository.MovieRepo;
import com.example.MS_Backend.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class MovieService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private MovieRepo movieRepo;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MovieService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

//    public JsonNode getGenres() throws Exception {
//        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key="+ apiKey + "&language=en-US";
//        String response = restTemplate.getForObject(url, String.class);
//
//        JsonNode json = objectMapper.readTree(response);
//        return json.get("genres");
//    }

    public List<Map<String, String>> searchMovies(String filters) throws Exception {
        String url = "http://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&" + filters;

        Random rand = new Random();
        int page = rand.nextInt(100) + 1;
        url += ("&page=" + page);

        String response = restTemplate.getForObject(url, String.class);

        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode results = jsonNode.get("results");

        if(results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No results found");
        }

        List<Map<String, String>> movies = new ArrayList<>();

        for(JsonNode json : results) {
            JsonNode title = json.get("title");
            JsonNode genres = json.get("genre_ids");
            JsonNode releaseDate = json.get("release_date");
            JsonNode overview = json.get("overview");

            Map<String, String> movie = new HashMap<>();
            movie.put("title", title.toString().replaceAll("\"", ""));
            movie.put("genres", genres.toString().replaceAll("[\"\\[\\]]", ""));
            movie.put("releaseDate", releaseDate.toString().replaceAll("\"", ""));
            movie.put("overview", overview.toString().replaceAll("\"", ""));

            movies.add(movie);
        }

        return movies;
    }

    public void addMovies(String username, List<Map<String, String>> movies) {
        User currUser = repo.findByUsername(username).orElse(null);

        if(currUser == null) {
            throw new RuntimeException("User not found");
        }

        List<Map<String, String>> currMovies = currUser.getMovies();
        currMovies.addAll(movies);
        currUser.setMovies(currMovies);

        repo.save(currUser);
    }

    public JsonNode getMovies(String username) throws JsonProcessingException {
        String results = movieRepo.findByUsername(username).orElse(null);
        return objectMapper.readTree(results);
    }

    public void deleteMovies(String username, List<String> titles) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));

        for(String title : titles) {
            Update update = new Update().pull("movies", new BasicDBObject("title", title));
            mongoTemplate.updateFirst(query, update, User.class);
        }
    }
}
