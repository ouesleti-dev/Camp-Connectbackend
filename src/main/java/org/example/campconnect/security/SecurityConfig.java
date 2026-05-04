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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Swagger
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

                        // ✅ Maintenance
                        .requestMatchers("/api/maintenance/predict/**").authenticated()
                        .requestMatchers("/api/maintenance-scheduler/dates/**").permitAll() // ← AJOUTÉ
                        .requestMatchers("/api/maintenance-scheduler/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/recommend").authenticated()

                        // ✅ Equipment
                        .requestMatchers(HttpMethod.GET, "/equipment/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/equipment/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/equipment/stats").authenticated()
                        .requestMatchers(HttpMethod.POST, "/equipment").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/verify/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/equipment/**").authenticated()

                        // ✅ Rental
                        .requestMatchers(HttpMethod.GET, "/rental/reserved-dates/**").permitAll() // ← MODIFIÉ
                        .requestMatchers(HttpMethod.POST, "/rental/request").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/accept/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/my-rentals").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/received").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/rental/**").authenticated()

                        // ✅ Reviews
                        .requestMatchers(HttpMethod.POST,   "/review/equipment/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/review/equipment/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/review/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/review/**").authenticated()

                        // ✅ Demand
                        .requestMatchers("/demand/**").authenticated()

                        // ✅ Story
                        .requestMatchers(HttpMethod.GET,    "/story/active").permitAll()
                        .requestMatchers(HttpMethod.GET,    "/story/my").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/story").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/story/*/apply-promo").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/story/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/story/**").authenticated()

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}