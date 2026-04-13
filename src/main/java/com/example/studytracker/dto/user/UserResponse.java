package com.example.studytracker.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 自分の情報取得APIのレスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

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
