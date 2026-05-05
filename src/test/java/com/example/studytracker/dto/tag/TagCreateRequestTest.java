package com.example.studytracker.dto.tag;

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
 * TagCreateRequestの単体テスト
 */
@DisplayName("TagCreateRequest 単体テスト")
class TagCreateRequestTest {

    private Validator validator;
    private TagCreateRequest tagCreateRequest;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        tagCreateRequest = new TagCreateRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：有効なタグ名の場合、バリデーションが通る")
        void validName_NoViolations() {
            // 準備
            tagCreateRequest.setName("Java");

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：nameがnullの場合、バリデーションエラー")
        void nullName_ValidationError() {
            // 準備
            tagCreateRequest.setName(null);

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("nameは必須です");
        }

        @Test
        @DisplayName("異常系：nameが空文字の場合、バリデーションエラー")
        void emptyName_ValidationError() {
            // 準備
            tagCreateRequest.setName("");

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("nameは必須です");
        }

        @Test
        @DisplayName("異常系：nameが空白のみの場合、バリデーションエラー")
        void blankName_ValidationError() {
            // 準備
            tagCreateRequest.setName("   ");

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("nameは必須です");
        }

        @Test
        @DisplayName("異常系：nameが50文字を超える場合、バリデーションエラー")
        void nameTooLong_ValidationError() {
            // 準備
            tagCreateRequest.setName("a".repeat(51));

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("nameは50文字で入力してください");
        }

        @Test
        @DisplayName("正常系：nameが50文字の場合、バリデーションが通る")
        void nameMaxLength_NoViolations() {
            // 準備
            tagCreateRequest.setName("a".repeat(50));

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：nameが1文字の場合、バリデーションが通る")
        void nameMinLength_NoViolations() {
            // 準備
            tagCreateRequest.setName("a");

            // 実行
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);

            // 検証
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("trim処理テスト")
    class TrimTests {

        @Test
        @DisplayName("nameの前後空白が除去される")
        void setName_Trimmed() {
            // 実行
            tagCreateRequest.setName("  Java  ");

            // 検証
            assertThat(tagCreateRequest.getName()).isEqualTo("Java");
        }

        @Test
        @DisplayName("nameがnullの場合、nullのまま")
        void setName_Null_RemainsNull() {
            // 実行
            tagCreateRequest.setName(null);

            // 検証
            assertThat(tagCreateRequest.getName()).isNull();
        }

        @Test
        @DisplayName("nameの前後空白除去後、バリデーションが通る")
        void setName_TrimmedThenValidated_NoViolations() {
            // 実行
            tagCreateRequest.setName("  Java  ");

            // 検証
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("nameが空白のみの場合、trim後に空文字になりバリデーションエラー")
        void setName_BlankOnly_TrimmedThenValidationError() {
            // 実行
            tagCreateRequest.setName("   ");

            // 検証
            Set<ConstraintViolation<TagCreateRequest>> violations = validator.validate(tagCreateRequest);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("nameは必須です");
        }
    }
}
