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
 * RegisterRequestの単体テスト
 */
@DisplayName("RegisterRequest 単体テスト")
class RegisterRequestTest {

    private Validator validator;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        registerRequest = new RegisterRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：すべての項目が有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：githubUsernameがnullでもバリデーションが通る")
        void validRequest_WithNullGithubUsername_NoViolations() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername(null);

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：usernameがnullの場合、バリデーションエラー")
        void nullUsername_ValidationError() {
            // 準備
            registerRequest.setUsername(null);
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが空文字の場合、バリデーションエラー")
        void emptyUsername_ValidationError() {
            // 準備
            registerRequest.setUsername("");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが空白のみの場合、バリデーションエラー")
        void blankUsername_ValidationError() {
            // 準備
            registerRequest.setUsername("   ");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは必須です");
        }

        @Test
        @DisplayName("異常系：usernameが50文字を超える場合、バリデーションエラー")
        void usernameTooLong_ValidationError() {
            // 準備
            registerRequest.setUsername("a".repeat(51));
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("usernameは50文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：usernameが50文字の場合、バリデーションが通る")
        void usernameMaxLength_NoViolations() {
            // 準備
            registerRequest.setUsername("a".repeat(50));
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：passwordがnullの場合、バリデーションエラー")
        void nullPassword_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword(null);
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは必須です");
        }

        @Test
        @DisplayName("異常系：passwordが空文字の場合、バリデーションエラー（@NotBlankと@Sizeの両方）")
        void emptyPassword_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証：@NotBlankと@Sizeの両方のエラーが発生
            assertThat(violations).hasSize(2);
            assertThat(violations).anyMatch(v -> v.getMessage().equals("passwordは必須です"));
            assertThat(violations).anyMatch(v -> v.getMessage().equals("passwordは8〜100文字で入力してください"));
        }

        @Test
        @DisplayName("異常系：passwordが空白のみの場合、バリデーションエラー（@NotBlankと@Sizeの両方）")
        void blankPassword_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("   ");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証：@NotBlankと@Sizeの両方のエラーが発生
            assertThat(violations).hasSize(2);
            assertThat(violations).anyMatch(v -> v.getMessage().equals("passwordは必須です"));
            assertThat(violations).anyMatch(v -> v.getMessage().equals("passwordは8〜100文字で入力してください"));
        }

        @Test
        @DisplayName("異常系：passwordが8文字未満の場合、バリデーションエラー")
        void passwordTooShort_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("pass");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは8〜100文字で入力してください");
        }

        @Test
        @DisplayName("正常系：passwordが8文字の場合、バリデーションが通る")
        void passwordMinLength_NoViolations() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("12345678");
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：passwordが100文字を超える場合、バリデーションエラー")
        void passwordTooLong_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("a".repeat(101));
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("passwordは8〜100文字で入力してください");
        }

        @Test
        @DisplayName("正常系：passwordが100文字の場合、バリデーションが通る")
        void passwordMaxLength_NoViolations() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("a".repeat(100));
            registerRequest.setGithubUsername("githubuser");

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：githubUsernameが100文字を超える場合、バリデーションエラー")
        void githubUsernameTooLong_ValidationError() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("a".repeat(101));

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("githubUsernameは100文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：githubUsernameが100文字の場合、バリデーションが通る")
        void githubUsernameMaxLength_NoViolations() {
            // 準備
            registerRequest.setUsername("testuser");
            registerRequest.setPassword("password123");
            registerRequest.setGithubUsername("a".repeat(100));

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：複数のバリデーションエラーが発生する場合")
        void multipleValidationErrors() {
            // 準備
            registerRequest.setUsername(null);
            registerRequest.setPassword("short");
            registerRequest.setGithubUsername("a".repeat(101));

            // 実行
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);

            // 検証
            assertThat(violations).hasSize(3);
        }
    }

    @Nested
    @DisplayName("trim処理テスト")
    class TrimTests {

        @Test
        @DisplayName("usernameの前後空白が除去される")
        void setUsername_Trimmed() {
            // 実行
            registerRequest.setUsername("  testuser  ");

            // 検証
            assertThat(registerRequest.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("usernameがnullの場合、nullのまま")
        void setUsername_Null_RemainsNull() {
            // 実行
            registerRequest.setUsername(null);

            // 検証
            assertThat(registerRequest.getUsername()).isNull();
        }

        @Test
        @DisplayName("passwordの前後空白が除去される")
        void setPassword_Trimmed() {
            // 実行
            registerRequest.setPassword("  password123  ");

            // 検証
            assertThat(registerRequest.getPassword()).isEqualTo("password123");
        }

        @Test
        @DisplayName("passwordがnullの場合、nullのまま")
        void setPassword_Null_RemainsNull() {
            // 実行
            registerRequest.setPassword(null);

            // 検証
            assertThat(registerRequest.getPassword()).isNull();
        }

        @Test
        @DisplayName("githubUsernameの前後空白が除去される")
        void setGithubUsername_Trimmed() {
            // 実行
            registerRequest.setGithubUsername("  githubuser  ");

            // 検証
            assertThat(registerRequest.getGithubUsername()).isEqualTo("githubuser");
        }

        @Test
        @DisplayName("githubUsernameがnullの場合、nullのまま")
        void setGithubUsername_Null_RemainsNull() {
            // 実行
            registerRequest.setGithubUsername(null);

            // 検証
            assertThat(registerRequest.getGithubUsername()).isNull();
        }
    }
}
