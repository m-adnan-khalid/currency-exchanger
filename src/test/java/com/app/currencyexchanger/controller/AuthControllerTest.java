package com.app.currencyexchanger.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.currencyexchanger.model.JwtResponse;
import com.app.currencyexchanger.model.UserLoginRequest;
import com.app.currencyexchanger.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class AuthControllerTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  private AuthController authController;

  private static final String TEST_USERNAME = "testUser";
  private static final String TEST_PASSWORD = "testPassword";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testWithValidCredentialsAndShouldReturnJwtResponse() {
    // Given
    UserLoginRequest request = new UserLoginRequest(TEST_USERNAME, TEST_PASSWORD);
    UserDetails userDetails = mock(UserDetails.class);
    String token = "mockJwtToken";
    when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenReturn(userDetails);
    when(authenticationManager.authenticate(any())).thenReturn(
        null); // Simulating successful authentication
    when(JwtUtil.generateToken(userDetails)).thenReturn(token);
    // When
    JwtResponse response = authController.login(request);
    // Then
    assertNotNull(response);
    verify(authenticationManager).authenticate(any());
    verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
  }

  @Test
  void testWithInvalidCredentialsAndShouldThrowBadCredentialsException() {
    // Given
    UserLoginRequest request = new UserLoginRequest(TEST_USERNAME, TEST_PASSWORD);

    when(authenticationManager.authenticate(any())).thenThrow(
        new BadCredentialsException("Invalid credentials"));

    // When & Then
    BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
        authController.login(request)
    );

    assertEquals("Invalid credentials", exception.getMessage());
    verify(authenticationManager).authenticate(any());
  }

  @Test
  void testWithUserNotFoundAndShouldThrowException() {
    // Given
    UserLoginRequest request = new UserLoginRequest(TEST_USERNAME, TEST_PASSWORD);

    when(authenticationManager.authenticate(any())).thenReturn(null); // Simulate successful auth
    when(userDetailsService.loadUserByUsername(TEST_USERNAME)).thenThrow(
        new UsernameNotFoundException("User not found"));

    // When & Then
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
        authController.login(request)
    );

    assertEquals("User not found", exception.getMessage());
    verify(authenticationManager).authenticate(any());
    verify(userDetailsService).loadUserByUsername(TEST_USERNAME);
  }
}
