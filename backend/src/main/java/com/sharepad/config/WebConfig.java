package com.sharepad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serves static frontend files from classpath:/static/ with an SPA fallback.
 * Any path that doesn't resolve to a real file returns index.html,
 * allowing client-side hash-based routing to work on direct URL access or refresh.
 * API and WebSocket paths are handled by their own controllers — this only affects
 * unrecognised paths that slip past all controllers.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    private List<String> getSanitizedAllowedOrigins() {
        List<String> origins = new ArrayList<>();
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(getSanitizedAllowedOrigins().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        // Serve the actual file if it exists (JS, CSS, images, etc.)
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // SPA fallback — return index.html for any unknown path
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
