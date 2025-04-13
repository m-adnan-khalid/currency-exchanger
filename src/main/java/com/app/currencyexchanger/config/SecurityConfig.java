package com.app.currencyexchanger.config;

import static com.app.currencyexchanger.constants.SecurityConstant.AFFILIATE_PASSWORD;
import static com.app.currencyexchanger.constants.SecurityConstant.AFFILIATE_USERNAME;
import static com.app.currencyexchanger.constants.SecurityConstant.CUSTOMER_PASSWORD;
import static com.app.currencyexchanger.constants.SecurityConstant.CUSTOMER_USERNAME;
import static com.app.currencyexchanger.constants.SecurityConstant.EMPLOYEE_PASSWORD;
import static com.app.currencyexchanger.constants.SecurityConstant.EMPLOYEE_USERNAME;
import static com.app.currencyexchanger.enums.Role.AFFILIATE;
import static com.app.currencyexchanger.enums.Role.CUSTOMER;
import static com.app.currencyexchanger.enums.Role.EMPLOYEE;

import com.app.currencyexchanger.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
        User.withUsername(EMPLOYEE_USERNAME)
            .password(passwordEncoder().encode(EMPLOYEE_PASSWORD))
            .roles(String.valueOf(EMPLOYEE))
            .build(),
        User.withUsername(AFFILIATE_USERNAME)
            .password(passwordEncoder().encode(AFFILIATE_PASSWORD))
            .roles(String.valueOf(AFFILIATE))
            .build(),
        User.withUsername(CUSTOMER_USERNAME)
            .password(passwordEncoder().encode(CUSTOMER_PASSWORD))
            .roles(String.valueOf(CUSTOMER))
            .build()
    );
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/api/auth/login"
            )
            .permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}