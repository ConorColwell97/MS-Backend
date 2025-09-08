package com.example.MS_Backend.controllers;

import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.MovieService;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MovieController {

    @Autowired
    private MovieService service;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/genres/{username}")
    public JsonNode findGenres(@PathVariable String username, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(verify(request, response, username)) {
            return service.getGenres();
        }
        throw new RuntimeException("Authentication error");
    }

    @GetMapping("/mymovies/{username}/{filters}")
    public List<Map<String, String>> getMovies(@PathVariable String username, @PathVariable String filters, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(verify(request, response, username)) {
            return service.searchMovies(filters);
        }
        throw new RuntimeException("Authentication error");
    }

    @PutMapping("/addmovies/{username}")
    public void addMovies(@PathVariable String username, @RequestBody List<Map<String, String>> movies, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.addMovies(username, movies);
        }
    }

    @GetMapping("/getmovies/{username}")
    public JsonNode getMoviesByUser(@PathVariable String username, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(verify(request, response, username)) {
            return service.getMovies(username);
        }
        throw new RuntimeException("Authentication error");
    }

    @PatchMapping("/deletemovie/{username}/{title}")
    public void removeMovie(@PathVariable String username, @PathVariable String title, HttpServletRequest request, HttpServletResponse response) {
        if(verify(request, response, username)) {
            service.deleteMovies(username, title);
        }
    }

    public boolean verify(HttpServletRequest request, HttpServletResponse response, String username) {
        Cookie[] cookies = request.getCookies();
        boolean expired = false;

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                String token = cookie.getValue();
                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(jwtService.getKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();
                    System.out.println("IAT: " + claims.getIssuedAt());
                    System.out.println("EXP: " + claims.getExpiration());

                } catch(ExpiredJwtException e) {
                    System.out.println("Token expired");
                    expired = true;
                } catch(JwtException e) {
                    System.out.println("This token is not valid");
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("Cookie expired or removed");
            return false;
        }

        if(expired) {
            String token = jwtService.generateToken(username);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(1800)
                    .sameSite("None")
                    .build();
            response.setHeader("Set-Cookie", cookie.toString());
        }
        return true;
    }

}
