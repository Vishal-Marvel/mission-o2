package com.lrc.missionO2.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**

The "JWTAuthEntryPoint" class is an implementation of the "AuthenticationEntryPoint" interface.
It handles unauthorized access to protected resources in a web application using JSON Web Tokens (JWT) for authentication.
The "commence" method is overridden to send an HTTP response with the appropriate error code and message when authentication fails.
It sends an HTTP 401 Unauthorized status code and includes the error message from the "AuthenticationException" in the response.
This class is typically used in conjunction with a JWT-based authentication mechanism to handle unauthorized requests.
When an unauthenticated request is made to a protected resource, this entry point is invoked to send an error response.
*/
@Component
public class JWTAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
