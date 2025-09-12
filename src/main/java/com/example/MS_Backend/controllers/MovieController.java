package com.example.MS_Backend.controllers;

import com.example.MS_Backend.services.JWTService;
import com.example.MS_Backend.services.MovieService;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

//    @GetMapping("/genres/{username}")
//    public JsonNode findGenres(@PathVariable String username, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        if(verify(request, response, username)) {
//            return service.getGenres();
//        }
//        throw new RuntimeException("Authentication error");
//    }

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

//    public boolean verify(HttpServletRequest request, HttpServletResponse response, String username) {
//        Cookie[] cookies = request.getCookies();
//        boolean expired = false;
//
//        if(cookies != null) {
//            for(Cookie cookie : cookies) {
//                String token = cookie.getValue();
//                try {
//                    Claims claims = Jwts.parser()
//                            .verifyWith(jwtService.getKey())
//                            .build()
//                            .parseSignedClaims(token)
//                            .getPayload();
//
//                } catch(ExpiredJwtException e) {
//                    System.out.println("Token expired");
//                    throw new RuntimeException(e);
//                } catch(JwtException e) {
//                    System.out.println("This token is not valid");
//                    throw new RuntimeException(e);
//                }
//            }
//        } else {
//            System.out.println("Cookie expired or removed");
//            return false;
//        }
//
//        if(expired) {
//            String token = jwtService.generateToken(username);
//
//            ResponseCookie cookie = ResponseCookie.from("token", token)
//                    .httpOnly(true)
//                    .secure(true)
//                    .path("/")
//                    .maxAge(1800)
//                    .sameSite("None")
//                    .build();
//            response.setHeader("Set-Cookie", cookie.toString());
//        }
//        return true;
//    }
}
