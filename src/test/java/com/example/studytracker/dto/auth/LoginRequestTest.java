package com.example.studytracker.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginRequestの単体テスト
 */
@DisplayName("LoginRequest 単体テスト")
class LoginRequestTest {

    private Validator validator;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        loginRequest = new LoginRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：すべての項目が有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：usernameがnullの場合、バリデーションエラー")
        void nullUsername_ValidationError() {
            // 準備
            loginRequest.setUsername(null);
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが空文字の場合、バリデーションエラー")
        void emptyUsername_ValidationError() {
            // 準備
            loginRequest.setUsername("");
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが空白のみの場合、バリデーションエラー")
        void blankUsername_ValidationError() {
            // 準備
            loginRequest.setUsername("   ");
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが50文字を超える場合、バリデーションエラー")
        void usernameTooLong_ValidationError() {
            // 準備
            loginRequest.setUsername("a".repeat(51));
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは50文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：usernameが50文字の場合、バリデーションが通る")
        void usernameMaxLength_NoViolations() {
            // 準備
            loginRequest.setUsername("a".repeat(50));
            loginRequest.setPassword("password123");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：passwordがnullの場合、バリデーションエラー")
        void nullPassword_ValidationError() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword(null);

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは必須です");
        }

        @Test
        @DisplayName("異常系：passwordが空文字の場合、バリデーションエラー")
        void emptyPassword_ValidationError() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword("");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは必須です");
        }

        @Test
        @DisplayName("異常系：passwordが空白のみの場合、バリデーションエラー")
        void blankPassword_ValidationError() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword("   ");

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは必須です");
        }

        @Test
        @DisplayName("異常系：passwordが100文字を超える場合、バリデーションエラー")
        void passwordTooLong_ValidationError() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword("a".repeat(101));

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは100文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：passwordが100文字の場合、バリデーションが通る")
        void passwordMaxLength_NoViolations() {
            // 準備
            loginRequest.setUsername("testuser");
            loginRequest.setPassword("a".repeat(100));

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：複数のバリデーションエラーが発生する場合")
        void multipleValidationErrors() {
            // 準備
            loginRequest.setUsername(null);
            loginRequest.setPassword(null);

            // 実行
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

            // 検証
            assertThat(violations).hasSize(2);
        }
    }

    @Nested
    @DisplayName("trim処理テスト")
    class TrimTests {

        @Test
        @DisplayName("usernameの前後空白が除去される")
        void setUsername_Trimmed() {
            // 実行
            loginRequest.setUsername("  testuser  ");

            // 検証
            assertThat(loginRequest.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("usernameがnullの場合、nullのまま")
        void setUsername_Null_RemainsNull() {
            // 実行
            loginRequest.setUsername(null);

            // 検証
            assertThat(loginRequest.getUsername()).isNull();
        }

        @Test
        @DisplayName("passwordの前後空白が除去される")
        void setPassword_Trimmed() {
            // 実行
            loginRequest.setPassword("  password123  ");

            // 検証
            assertThat(loginRequest.getPassword()).isEqualTo("password123");
        }

        @Test
        @DisplayName("passwordがnullの場合、nullのまま")
        void setPassword_Null_RemainsNull() {
            // 実行
            loginRequest.setPassword(null);

            // 検証
            assertThat(loginRequest.getPassword()).isNull();
        }
    }
}
