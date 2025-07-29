package com.example.online_car_service_station_backend.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Justification: This class implements Spring Security's AuthenticationEntryPoint interface.
// It's triggered whenever an unauthenticated user tries to access a secured HTTP resource.
// Instead of redirecting to a login page (typical for web apps), for a REST API, we send
// an HTTP 401 Unauthorized response with a clear error message.
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // Justification: This method is called when an unauthenticated user tries to access a protected resource.
    // It logs the authentication error and sends a 401 (Unauthorized) HTTP status code with a JSON error message.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Justification: For REST APIs, we return a JSON error instead of redirecting.
        // Sets the HTTP status to 401 (Unauthorized).
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        // Optionally, for more detailed JSON response:
        // response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // final Map<String, Object> body = new HashMap<>();
        // body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        // body.put("error", "Unauthorized");
        // body.put("message", authException.getMessage());
        // body.put("path", request.getServletPath());
        // final ObjectMapper mapper = new ObjectMapper();
        // mapper.writeValue(response.getOutputStream(), body);
    }
}