package com.example.studytracker.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT設定値をapplication.yamlからバインドするクラス
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * JWT署名に使用するシークレットキー
     */
    private String secret;

    /**
     * JWT有効期限（ミリ秒）
     * デフォルト: 1時間（3600000ms）
     */
    private long expiration = 3600000;
}
