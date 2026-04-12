package com.example.studytracker.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ログインリクエストDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
