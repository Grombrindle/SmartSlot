package com.appointment.system.security;

import com.appointment.system.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

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
import static org.springframework.http.HttpMethod.GET;

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
    // http
    // .csrf(csrf -> csrf.disable())
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/api/auth/login").permitAll()
    // .requestMatchers("/h2-console/**").permitAll()
    // .requestMatchers("/api/users").permitAll() // Allow user registration
    // .requestMatchers("/api/users/**").authenticated()
    // .anyRequest().authenticated()
    // )
    // .headers(headers -> headers
    // .frameOptions(frame -> frame.disable()) // Disable frame options completely
    // // Or use request matcher to only disable for H2 console:
    // // .addHeaderWriter(new XFrameOptionsHeaderWriter(
    // // new AllowFromStrategy() {
    // // @Override
    // // public String getAllowFromValue(HttpServletRequest request) {
    // // return "http://localhost:8080";
    // // }
    // // }
    // // ))
    // )
    // .addFilterBefore(jwtAuthenticationFilter,
    // UsernamePasswordAuthenticationFilter.class);

    // return http.build();
    // }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // .ignoringRequestMatchers("/h2-console/**", "/api/**")
                        .ignoringRequestMatchers("/h2-console/**", "/api/**", "/ws-email/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(GET, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/", "/login", "/register", "/register/**",
                                "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // API endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/users").permitAll()
                        .requestMatchers("/ws-email/**", "/ws/**").permitAll() // Add WebSocket endpoint
                        .requestMatchers("/topic/**", "/app/**").permitAll()

                        // Dashboard access
                        .requestMatchers("/dashboard/admin").hasRole("ADMIN")
                        .requestMatchers("/dashboard/staff").hasRole("STAFF")
                        .requestMatchers("/dashboard/customer").hasRole("CUSTOMER")
                        .requestMatchers("/dashboard").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            response.setContentType("application/json");
                            response.getWriter()
                                    .write("{\"error\": \"Authentication required: you are not logged in\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            response.setContentType(";application/json");
                            response.getWriter().write("{\"error\": \"Access denied: you do not have permission\"}");
                        }))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()));

        return http.build();
    }

}