package com.example.online_car_service_station_backend.security.jwt;

import com.example.online_car_service_station_backend.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Justification: This custom filter extends OncePerRequestFilter, ensuring it runs only once per HTTP request.
// Its role is to intercept every incoming request, extract the JWT from the Authorization header,
// validate it, and if valid, set the user's authentication information in Spring Security's context.
// This allows subsequent security checks (e.g., @PreAuthorize) to work correctly.
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // Justification: This is the core logic of the filter. It's executed for every incoming request.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Extract JWT from the Authorization header (Bearer token)
            String jwt = parseJwt(request);

            // 2. If JWT exists and is valid, authenticate the user
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Load user details from our custom UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create an authentication token for the user
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Credentials are null as it's already authenticated via JWT
                                userDetails.getAuthorities()); // User's roles/authorities

                // Set authentication details (e.g., remote address, session ID)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in Spring Security's SecurityContext.
                // This is crucial: once set, Spring Security considers the user authenticated for the current request.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Justification: Continues the filter chain, allowing the request to proceed to other filters
        // or the target servlet/controller.
        filterChain.doFilter(request, response);
    }

    // Justification: Helper method to extract the JWT from the Authorization header.
    // It checks for the "Bearer " prefix.
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Extract token after "Bearer "
        }

        return null;
    }
}