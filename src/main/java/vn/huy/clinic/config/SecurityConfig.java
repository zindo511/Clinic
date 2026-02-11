package vn.huy.clinic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // active spring web security
@EnableMethodSecurity // Kích hoạt phân quyền @PreAuthorize ở Controller
@RequiredArgsConstructor
public class SecurityConfig {

    // Tiêm (inject) trạm gác tự chế của chúng ta vào
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 1. Kích hoạt tính năng CORS trong Spring Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**", // Cho phép đăng nhập/đăng ký
                                "/v3/api-docs/**",      // API docs (JSON)
                                "/swagger-ui/**",       // Giao diện Swagger
                                "/swagger-ui.html"      // Trang chủ Swagger
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 2. Quan trọng: Chỉ định rõ địa chỉ FE
        configuration.setAllowedOrigins(List.of("http://localhost:9090", "http://localhost:3000"));

        // 3. Các phương thức được phép
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 4. Cho phép các Header. *: cho phép gửi Content-type, Authorization, v.v. (hành lý mang theo của FE)
        configuration.setAllowedHeaders(List.of("*"));

        // 5. Cho phép gửi Cookie/Credentials. Nếu set true thì allowedOrigins KHÔNG được để *
        configuration.setAllowCredentials(true);

        // 6. Áp dụng cấu hình này cho mọi đường dẫn API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
