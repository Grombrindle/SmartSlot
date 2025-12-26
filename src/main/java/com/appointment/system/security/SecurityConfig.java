package com.appointment.system.security;

import com.appointment.system.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
  
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(session -> 
    //             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers("/api/auth/login").permitAll()
    //             .requestMatchers("/h2-console/**").permitAll()
    //             .requestMatchers("/api/users").permitAll() // Allow user registration
    //             .requestMatchers("/api/users/**").authenticated()
    //             .anyRequest().authenticated()
    //         )
    //           .headers(headers -> headers
    //         .frameOptions(frame -> frame.disable()) // Disable frame options completely
    //         // Or use request matcher to only disable for H2 console:
    //         // .addHeaderWriter(new XFrameOptionsHeaderWriter(
    //         //     new AllowFromStrategy() {
    //         //         @Override
    //         //         public String getAllowFromValue(HttpServletRequest request) {
    //         //             return "http://localhost:8080";
    //         //         }
    //         //     }
    //         // ))
    //     )
    //         .addFilterBefore(jwtAuthenticationFilter, 
    //             UsernamePasswordAuthenticationFilter.class);
        
    //     return http.build();
    // }
     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Staff endpoints (Admin + Staff)
                .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF")
                
                // Customer endpoints (Admin + Staff + Customer)
                .requestMatchers("/api/customers/profile").hasRole("CUSTOMER")
                .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "STAFF")
                
                // Fallback - all other endpoints require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

}