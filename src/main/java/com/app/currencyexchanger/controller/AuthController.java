package com.app.currencyexchanger.controller;

import static com.app.currencyexchanger.constants.ApiUrl.LOGIN_URL;
import static com.app.currencyexchanger.util.JwtUtil.generateToken;

import com.app.currencyexchanger.model.JwtResponse;
import com.app.currencyexchanger.model.UserLoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "APIs for JWT token generation and user authentication")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  public AuthController(
      AuthenticationManager authenticationManager, UserDetailsService userDetailsService
  ) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
  }

  @PostMapping(LOGIN_URL)
  public JwtResponse login(@RequestBody UserLoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String token = generateToken(userDetails);
    return new JwtResponse(token);
  }
}
