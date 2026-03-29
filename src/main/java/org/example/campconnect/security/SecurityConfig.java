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
                        // ✅ Swagger - IMPORTANT : ces lignes doivent être en premier
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
                        // ✅ Equipement
                        .requestMatchers(HttpMethod.GET, "/equipment/verified").permitAll()
                        .requestMatchers(HttpMethod.POST, "/equipment").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/equipment/unverified").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/verify/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/equipment/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/equipment/**").authenticated()
                        // rental
                        .requestMatchers(HttpMethod.POST, "/rental/request").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/accept/**").authenticated() // ✅ PRIORITÉ
                        .requestMatchers(HttpMethod.GET, "/rental/my-rentals").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/received").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rental/reserved-dates/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rental/**").authenticated()   // ✅ AJOUTÉ
                        .requestMatchers(HttpMethod.DELETE, "/rental/**").authenticated() // ✅ AJOUTÉ
                        // Reviews
                        .requestMatchers(HttpMethod.POST,   "/review/equipment/**").authenticated()  // ← POST en PREMIER
                        .requestMatchers(HttpMethod.GET,    "/review/equipment/**").permitAll()       // ← GET en SECOND
                        .requestMatchers(HttpMethod.PUT,    "/review/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/review/**").authenticated()
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
