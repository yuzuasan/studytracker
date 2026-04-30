package com.example.studytracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientの設定クラス
 * <p>
 * GitHub API呼び出し用のWebClient Beanを定義する。
 * AuthorizationヘッダにGitHub PATを設定する。
 * </p>
 */
@Configuration
public class WebClientConfig {

    @Value("${github.api.base-url}")
    private String githubApiBaseUrl;

    @Value("${github.api.token}")
    private String githubToken;

    /**
     * GitHub API用のWebClient Bean
     * <p>
     * ベースURLとAuthorizationヘッダ（PAT）を設定したWebClientを生成する。
     * </p>
     *
     * @return 設定済みのWebClient
     */
    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl(githubApiBaseUrl)
                .defaultHeader("Authorization", "Bearer " + githubToken)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }
}
