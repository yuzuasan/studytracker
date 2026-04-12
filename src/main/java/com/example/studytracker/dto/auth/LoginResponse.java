package com.example.studytracker.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ログインレスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * アクセストークン（JWT）
     */
    private String accessToken;

    /**
     * トークンタイプ
     * デフォルト: "Bearer"
     */
    private String tokenType;
}
