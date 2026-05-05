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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← ADDED
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**", "/v3/api-docs",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        // Auth
                        .requestMatchers("/auth/**").permitAll()
                        // WebSocket
                        .requestMatchers("/ws/**").permitAll()
                        // Notifications
                        .requestMatchers("/api/notifications/**").authenticated()
                        // Recommendations
                        .requestMatchers(HttpMethod.GET, "/api/recommendations/**").permitAll()
                        // Maintenance / AI
                        .requestMatchers("/api/maintenance/predict/**").authenticated()
                        .requestMatchers("/api/maintenance-scheduler/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/recommend").authenticated()
                        // Equipment
                        .requestMatchers(HttpMethod.GET, "/equipment/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/equipment/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/equipment").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/stats").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/verify/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/equipment/**").authenticated()
                        // Rental
                        .requestMatchers(HttpMethod.POST, "/rental/request").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/accept/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/my-rentals").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/received").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/reserved-dates/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/rental/**").authenticated()
                        // Reviews
                        .requestMatchers(HttpMethod.POST, "/review/equipment/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/review/equipment/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/review/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/review/**").authenticated()
                        // Demand
                        .requestMatchers("/demand/**").authenticated()
                        // Stories
                        .requestMatchers(HttpMethod.GET, "/story/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/story/my").authenticated()
                        .requestMatchers(HttpMethod.POST, "/story").authenticated()
                        .requestMatchers(HttpMethod.POST, "/story/*/apply-promo").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/story/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/story/**").authenticated()
                        // Transport
                        .requestMatchers("/trips/upcoming").authenticated()
                        .requestMatchers("/transport-ai/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/vehicles/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/vehicles/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/vehicles/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/vehicles/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/options/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/options/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/options/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/options/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/trips/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/trips/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/trips/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/trips/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/transport-ads/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/transport-ads/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/transport-ads/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/transport-ads/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/reservations/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/reservations/**").authenticated()
                        // Campgrounds
                        .requestMatchers(HttpMethod.DELETE, "/campings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/campings/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/campings/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/campings/**").authenticated()
                        // Events
                        .requestMatchers(HttpMethod.POST, "/events/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/events/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/events/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/events/**").authenticated()
                        // Activities
                        .requestMatchers(HttpMethod.POST, "/activities/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/activities/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/activities/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/activities/**").authenticated()
                        // Community
                        .requestMatchers("/posts/**").authenticated()
                        .requestMatchers("/comments/**").authenticated()
                        .requestMatchers("/responses/**").authenticated()
                        .requestMatchers("/participations/**").authenticated()
                        .requestMatchers("/tickets/**").authenticated()
                        .requestMatchers("/stats/**").authenticated()
                        .requestMatchers("/ml/**").hasAnyRole("CAMPOWNER", "ADMIN")
                        // Products & Orders
                        .requestMatchers(HttpMethod.GET, "/api/products/approved").permitAll()
                        .requestMatchers("/api/orders/confirmed").hasRole("ADMIN")
                        .requestMatchers("/api/orders/*/approve").hasRole("ADMIN")
                        .requestMatchers("/api/orders/*/reject").hasRole("ADMIN")
                        .requestMatchers("/api/orders/*/confirm").authenticated()
                        .requestMatchers("/api/orders/*/cancel").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/products/pending").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/*/reject").hasRole("ADMIN")
                        .requestMatchers("/api/deliveries/**").authenticated()
                        .requestMatchers("/api/products/**").authenticated()
                        // Role-based prefixes
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/camp/**").hasRole("CAMPOWNER")
                        .requestMatchers("/camper/**").hasRole("CAMPER")
                        .requestMatchers("/delivery/**").hasRole("DELIVERYPERSON")
                        .requestMatchers("/partner/**").hasRole("PARTNER")
                        // Catch-all — MUST be last
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization")); // ← ADDED for JWT
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}