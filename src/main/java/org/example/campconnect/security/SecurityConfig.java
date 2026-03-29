package org.example.campconnect.security;

import lombok.RequiredArgsConstructor;
import org.example.campconnect.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                                // ✅ Swagger - IMPORTANT : ces lignes doivent être en premier
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                                // ✅ Auth endpoints
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/api/orders/**").authenticated()
                                // Products - public
                                .requestMatchers(HttpMethod.GET, "/api/products/approved").permitAll()
                                .requestMatchers("/api/orders/confirmed").hasRole("ADMIN")
                                .requestMatchers("/api/orders/*/approve").hasRole("ADMIN")
                                .requestMatchers("/api/orders/*/reject").hasRole("ADMIN")
                                .requestMatchers("/api/orders/*/confirm").authenticated()
                                .requestMatchers("/api/orders/*/cancel").authenticated()

// Products - admin only
                                .requestMatchers(HttpMethod.GET, "/api/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/products/pending").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/products/*/approve").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/products/*/reject").hasRole("ADMIN")
                                .requestMatchers("/api/deliveries/**").authenticated()
// Products - authenticated
                                .requestMatchers("/api/products/**").authenticated()
                                // ✅ Rôles
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/camp/**").hasRole("CAMPOWNER")
                                .requestMatchers("/camper/**").hasRole("CAMPER")
                                .requestMatchers("/delivery/**").hasRole("DELIVERYPERSON")
                                .requestMatchers("/partner/**").hasRole("PARTNER")
                                .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // ✅ CHANGER CETTE LIGNE
//        config.setAllowedOriginPatterns(List.of("*")); // ← pas setAllowedOrigins !
//
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}

