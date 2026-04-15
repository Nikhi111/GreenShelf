package com.example.GreenSelf.filter;

import com.example.GreenSelf.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ FIXED: Only skip public endpoints, not all /api/user
        if (path.startsWith("/api/products") ||
                path.startsWith("/api/auth") ||
                path.equals("/api/user/register") ||  // ✅ Only skip registration
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/error")) {

            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("===== JWT FILTER START =====");
        System.out.println("Processing path: " + path);

        String authHeader = request.getHeader("Authorization");
        System.out.println("STEP 1 Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);
            System.out.println("STEP 2 Token: " + token);

            boolean isValid = jwtUtil.isTokenValid(token);
            System.out.println("STEP 3 Valid: " + isValid);

            if (isValid) {

                String username = jwtUtil.extractUsername(token);
                System.out.println("STEP 4 Username: " + username);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("STEP 5 Authentication set for: " + username);
            }
        }

        System.out.println("===== JWT FILTER END =====");

        filterChain.doFilter(request, response);
    }
}