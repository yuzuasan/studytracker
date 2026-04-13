package com.example.studytracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import com.example.studytracker.security.JwtAuthenticationFilter;

/**
 * Spring Securityの設定クラス
 * APIとしてのアクセス制御を定義する
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * ObjectMapperのBean設定
     * JSON変換に使用
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * CORS設定（開発環境用：全許可）
     * 
     * @return CORS設定
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * PasswordEncoderのBean設定
     * BCryptを使用してパスワードをハッシュ化する
     * 
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * セキュリティフィルタチェーンの設定
     *
     * @param http HttpSecurity設定オブジェクト
     * @param jwtAuthenticationFilter JWT認証フィルタ（引数で受け取り）
     * @return 構築されたSecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
            // CSRFを無効化（REST APIのため）
            .csrf(AbstractHttpConfigurer::disable)
            // CORSを有効化（開発環境用：全許可）
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // セッション管理をステートレスに設定（APIとして適切）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // アクセス制御の設定
            .authorizeHttpRequests(auth -> auth
                // ルートパスを許可（ログイン画面の無効化）
                .requestMatchers("/").permitAll()
                // H2コンソールを許可
                .requestMatchers("/h2-console/**").permitAll()
                // Swagger UIを許可
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                // API Docsを許可（スラッシュあり・なし両方）
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll()
                // 認証エンドポイントを許可
                .requestMatchers("/auth/**").permitAll()
                // 静的リソースを許可
                .requestMatchers("/webjars/**", "/favicon.ico").permitAll()
                // それ以外は認証が必要
                .anyRequest().authenticated()
            )
            // JWT認証フィルタを追加（UsernamePasswordAuthenticationFilterの前に実行）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // H2コンソールのフレーム利用を許可
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
