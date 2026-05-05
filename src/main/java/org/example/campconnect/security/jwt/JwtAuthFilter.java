package org.example.campconnect.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.security.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String method = request.getMethod();
        String uri = request.getRequestURI();

        System.out.println("=== JWT FILTER ===");
        System.out.println("URI: " + request.getRequestURI());
        System.out.println("Auth Header: " + authHeader);
        System.out.println(">>> REQUEST: " + method + " " + uri);
        System.out.println(">>> AUTH HEADER: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> NO TOKEN - skipping");
            filterChain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);
        String email;

        try {
            email = jwtService.extractEmail(token);
            System.out.println("Email extracted: " + email);

        } catch (Exception e) {
            System.out.println("Token parse error: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }


        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            System.out.println("UserDetails loaded: " + userDetails.getUsername());
            System.out.println("Authorities: " + userDetails.getAuthorities());
            System.out.println("Token valid: " + jwtService.isTokenValid(token, userDetails));

            if (jwtService.isTokenValid(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authentication SET ✅");
            } else {
                System.out.println("Token INVALID ❌");
            }
        }

        filterChain.doFilter(request, response);

    }
}
