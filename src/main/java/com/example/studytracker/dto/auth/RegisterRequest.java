package com.example.studytracker.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * ユーザー登録リクエストDTO
 */
@Getter
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

    /**
     * ユーザー名を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param username ユーザー名
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * パスワードを設定する
     * バリデーション前に前後空白を除去する
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * GitHubユーザー名を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param githubUsername GitHubユーザー名
     */
    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername == null ? null : githubUsername.trim();
    }
}
