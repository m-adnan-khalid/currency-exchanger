package com.app.currencyexchanger.util;

import static java.util.Objects.isNull;

import com.app.currencyexchanger.enums.Role;
import com.app.currencyexchanger.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for retrieving the currently authenticated user from the Spring Security context.
 * <p>
 * This class helps extract user information, including username and role, from the security
 * context.
 */
public class SecurityUtil {

  private SecurityUtil() {
    // Private constructor to prevent instantiation of this utility class.
  }

  public static User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (isNull(authentication) || !authentication.isAuthenticated()) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof org.springframework.security.core.userdetails.User user) {
      User securityUser = new User();
      securityUser.setUsername(user.getUsername());
      // Extract role from authorities
      user.getAuthorities().stream()
          .findFirst()
          .ifPresent(authority -> {
            String role = authority.getAuthority().replace("ROLE_", "");
            securityUser.setRole(Role.valueOf(role));
          });
      return securityUser;
    }
    return null;
  }

}
