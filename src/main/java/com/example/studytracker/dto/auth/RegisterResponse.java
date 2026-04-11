package com.example.studytracker.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ユーザー登録レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    /**
     * ユーザーID
     */
    private Long userId;

    /**
     * ユーザー名
     */
    private String username;

    /**
     * GitHubユーザー名
     */
    private String githubUsername;
}
