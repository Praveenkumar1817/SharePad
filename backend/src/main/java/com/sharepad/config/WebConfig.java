package com.sharepad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Serves static frontend files from classpath:/static/ with an SPA fallback.
 * Any path that doesn't resolve to a real file returns index.html,
 * allowing client-side hash-based routing to work on direct URL access or refresh.
 * API and WebSocket paths are handled by their own controllers — this only affects
 * unrecognised paths that slip past all controllers.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

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
