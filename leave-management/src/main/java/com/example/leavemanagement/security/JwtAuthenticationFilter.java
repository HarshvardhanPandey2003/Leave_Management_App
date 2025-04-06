package com.example.leavemanagement.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// Extending OncePerRequestFilter: This ensures that the filter is executed only once per request.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Uses jwtUtils.extractUsername(jwt) to get the username (in this case, the email) from the token.
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtils.extractUsername(jwt);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token has expired");
            } catch (MalformedJwtException e) {
                logger.error("Invalid JWT Token");
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // If email is extracted and there's no authentication in the context
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Get the user details after ensuring the jwt has a bearer and then using the email to get all the deails
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // If token is valid, set up Spring Security authentication
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // Create an authentication token using the user details and set it in the SecurityContextHolder
                // Security ContextHolder in simple words means the current user session and the authentication details of the user
                // Reason for using SecurityContextHolder is to store the authentication details of the user in the current thread context,
                // so that it can be accessed later in the application.

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // WebAuthenticationDetailsSource is used to create an object that contains the details of the authentication request. 
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
