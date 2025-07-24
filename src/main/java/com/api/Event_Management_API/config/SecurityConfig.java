package com.api.Event_Management_API.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.Event_Management_API.security.JwtAuthenticationFilter;

// @EnableMethodSecurity(prePostEnabled = true)
// @Configuration
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//     }
    
//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http
//             .cors(Customizer.withDefaults()) // Allow cors
//             .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
//             .sessionManagement(session -> session
//                                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // since JWT is stateless
//             )
//             .authorizeHttpRequests(auth -> auth
//                 // allow for these path
//                 .requestMatchers("/api/auth/**").permitAll()
//                 .requestMatchers("/api/danhmucsukien/get/**").permitAll()
//                 .requestMatchers("/api/sukien/get/**").permitAll()
//                 .requestMatchers("/api/danhgia/sukien/*/get/all").permitAll()
//                 .requestMatchers("/api/ticket/create").permitAll()
//                 .anyRequest().authenticated()
//             )
//             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//         return http.build();
//     }
// }

// For production

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/danhmucsukien/get/**",
                    "/api/sukien/get/**",
                    "/api/danhgia/sukien/*/get/all",
                    "/api/ticket/create"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}