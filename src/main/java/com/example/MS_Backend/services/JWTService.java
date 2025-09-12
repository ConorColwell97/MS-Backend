package com.example.MS_Backend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    private final String secretKey;

    public JWTService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public void generateToken(String username, HttpServletResponse response) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .signWith(getKey())
                .compact();

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1860)
                .sameSite("None")
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void verifyToken(HttpServletRequest request, HttpServletResponse response, String username) {
        Cookie[] cookies = request.getCookies();
        boolean expired = false;

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                String token = cookie.getValue();
                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(getKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                } catch(ExpiredJwtException e) {
                    System.out.println("Token expired");
                    expired = true;
                } catch(JwtException e) {
                    System.out.println("This token is not valid");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an authorized user");
                }
            }
        } else {
            System.out.println("Cookie expired or removed");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an authorized user");
        }

        if(expired) {
            generateToken(username, response);
        }
    }
}
