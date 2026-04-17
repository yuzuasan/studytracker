package com.example.studytracker.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ユーザー登録リクエストDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /**
     * ユーザー名
     * 必須 / 1〜50文字
     */
    @NotBlank(message = "usernameは必須です")
    @Size(max = 50, message = "usernameは50文字以内で入力してください")
    private String username;

    /**
     * パスワード
     * 必須 / 8〜100文字
     */
    @NotBlank(message = "passwordは必須です")
    @Size(min = 8, max = 100, message = "passwordは8〜100文字で入力してください")
    private String password;

    /**
     * GitHubユーザー名
     * 任意 / 1〜100文字
     */
    @Size(max = 100, message = "githubUsernameは100文字以内で入力してください")
    private String githubUsername;
}
