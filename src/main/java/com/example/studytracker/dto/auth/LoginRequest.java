package com.example.studytracker.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * ログインリクエストDTO
 */
@Getter
public class LoginRequest {

    /**
     * ユーザー名
     * 必須 / 最大50文字
     */
    @NotBlank(message = "usernameは必須です")
    @Size(max = 50, message = "usernameは50文字以内で入力してください")
    private String username;

    /**
     * パスワード
     * 必須 / 最大100文字
     */
    @NotBlank(message = "passwordは必須です")
    @Size(max = 100, message = "passwordは100文字以内で入力してください")
    private String password;

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
}
