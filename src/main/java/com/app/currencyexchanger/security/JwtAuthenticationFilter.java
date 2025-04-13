package com.app.currencyexchanger.security;

import static com.app.currencyexchanger.util.JwtUtil.extractUsername;
import static com.app.currencyexchanger.util.JwtUtil.isTokenValid;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    String username = null;
    String jwt = null;
    try {
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7);
        LOGGER.debug("Extracted JWT from header: {}", jwt);
        username = extractUsername(jwt);
        LOGGER.debug("Extracted username from JWT: {}", username);
      }
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (isTokenValid(jwt, userDetails)) {
          LOGGER.info("Token is valid for user: {}", username);
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          LOGGER.warn("Invalid or expired token for user: {}", username);
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.getWriter().write("Invalid or expired token");
          return;
        }
      }
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      LOGGER.error("JWT authentication failed: {}", ex.getMessage(), ex);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getWriter().write("Unauthorized: " + ex.getMessage());
    }
  }
}
