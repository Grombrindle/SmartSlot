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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**", "/api/**")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers("/", "/login", "/register", "/register/**",
                           "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            
            // API endpoints
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/users").permitAll()
            
            // Dashboard access
            .requestMatchers("/dashboard/admin").hasRole("ADMIN")
            .requestMatchers("/dashboard/staff").hasRole("STAFF")
            .requestMatchers("/dashboard/customer").hasRole("CUSTOMER")
            .requestMatchers("/dashboard").authenticated()
            
            // All other requests require authentication
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/login?error=true")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        )
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        )
        .addFilterBefore(jwtAuthenticationFilter, 
            UsernamePasswordAuthenticationFilter.class)
        .headers(headers -> headers
            .frameOptions(frame -> frame.disable())
        );

    return http.build();
}


}