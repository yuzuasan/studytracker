package com.example.studytracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Securityの設定クラス
 * APIとしてのアクセス制御を定義する
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
     * セキュリティフィルタチェーンの設定
     * 
     * @param http HttpSecurity設定オブジェクト
     * @return 構築されたSecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
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
                // それ以外は認証が必要（現在はフィルタ未実装のため実質許可）
                .anyRequest().authenticated()
            )
            // H2コンソールのフレーム利用を許可
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
