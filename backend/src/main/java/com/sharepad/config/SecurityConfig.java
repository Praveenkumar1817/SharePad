package com.sharepad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    // After OAuth login, send the user back to the frontend (Vercel), not the backend
    @Value("${frontend.redirect-url:https://sharepad-nu.vercel.app}")
    private String frontendRedirectUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Public API endpoints
                        .requestMatchers("/api/auth/me", "/api/notes/{noteKey}").permitAll()
                        // WebSocket
                        .requestMatchers("/ws/**").permitAll()
                        // All other paths — static assets and SPA handled by WebConfig
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        // Redirect to Vercel frontend after successful Google login
                        .defaultSuccessUrl(frontendRedirectUrl, true));
        return http.build();
    }

    private List<String> getSanitizedAllowedOrigins() {
        List<String> origins = new java.util.ArrayList<>();
        if (allowedOrigins != null) {
            for (String origin : allowedOrigins) {
                if (origin != null && !origin.trim().isEmpty()) {
                    String trimmed = origin.trim();
                    origins.add(trimmed);
                    if (trimmed.endsWith("/")) {
                        origins.add(trimmed.substring(0, trimmed.length() - 1));
                    } else {
                        origins.add(trimmed + "/");
                    }
                }
            }
        }
        // Always allow localhost and 127.0.0.1 for local testing
        if (!origins.contains("http://localhost:5500")) {
            origins.add("http://localhost:5500");
            origins.add("http://localhost:5500/");
        }
        if (!origins.contains("http://127.0.0.1:5500")) {
            origins.add("http://127.0.0.1:5500");
            origins.add("http://127.0.0.1:5500/");
        }
        return origins;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(getSanitizedAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
