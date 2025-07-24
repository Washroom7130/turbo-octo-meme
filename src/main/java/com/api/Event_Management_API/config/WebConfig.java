package com.api.Event_Management_API.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig {
    
//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer() {
//             @Override
//             public void addCorsMappings(CorsRegistry corsRegistry) {
//                 corsRegistry.addMapping("/**") // allow all routes
//                     .allowedOrigins("http://localhost:3000")
//                     .allowedMethods("*")
//                     .allowedHeaders("*")
//                     .allowCredentials(true); // for credentials: include in frontend
//             }
//         };
//     }
// }

// For production

@Configuration
public class WebConfig {

    // Adjust frontend domain in production
    private static final String FRONTEND_ORIGIN = "http://localhost:3000";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry cors) {
                cors.addMapping("/**")
                    .allowedOrigins(FRONTEND_ORIGIN)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("Cookie", "Content-Type")
                    .allowCredentials(true)
                    .maxAge(3600); // cache preflight response for 1 hour
            }
        };
    }
}
