package com.app.currencyexchanger.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtUtil {

  private JwtUtil() {

  }

  private static final String SECRET_KEY = "your-256-bit-secret-which-should-be-long-enough-to-meet-HMAC-256-standards";

  private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

  private static Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  public static String generateToken(UserDetails userDetails) {
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public static String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public static boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String extractedUsername = extractUsername(token);
      return extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private static boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private static Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

}
