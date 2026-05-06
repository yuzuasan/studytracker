package com.example.studytracker.dto.goal;

import com.example.studytracker.testutil.TestUtil;
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
 * GoalCreateRequestの単体テスト
 */
@DisplayName("GoalCreateRequest 単体テスト")
class GoalCreateRequestTest {

    private Validator validator;
    private GoalCreateRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new GoalCreateRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：すべての項目が有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：monthがnullの場合、バリデーションエラー")
        void nullMonth_ValidationError() {
            // 準備
            request.setMonth(null);
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("monthは必須です");
        }

        @Test
        @DisplayName("異常系：monthが空文字の場合、バリデーションエラー（@NotBlankと@Patternの両方）")
        void emptyMonth_ValidationError() {
            // 準備
            request.setMonth("");
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証（@NotBlankと@Patternの両方がエラーになる）
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting("message")
                    .contains("monthは必須です", "monthはYYYY-MM形式で入力してください");
        }

        @Test
        @DisplayName("異常系：monthが空白のみの場合、バリデーションエラー（@NotBlankと@Patternの両方）")
        void blankMonth_ValidationError() {
            // 準備
            request.setMonth("   ");
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証（@NotBlankと@Patternの両方がエラーになる）
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting("message")
                    .contains("monthは必須です", "monthはYYYY-MM形式で入力してください");
        }

        @Test
        @DisplayName("異常系：monthがYYYY-MM形式でない場合、バリデーションエラー")
        void invalidMonthFormat_ValidationError() {
            // 準備
            request.setMonth("2024/01");
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("monthはYYYY-MM形式で入力してください");
        }

        @Test
        @DisplayName("異常系：monthがYYYY-MM形式でない場合（月が1桁）、バリデーションエラー")
        void invalidMonthFormat_SingleDigitMonth_ValidationError() {
            // 準備
            request.setMonth("2024-1");
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("monthはYYYY-MM形式で入力してください");
        }

        @Test
        @DisplayName("異常系：targetMinutesがnullの場合、バリデーションエラー")
        void nullTargetMinutes_ValidationError() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", null);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは必須です");
        }

        @Test
        @DisplayName("異常系：targetMinutesが0の場合、バリデーションエラー")
        void targetMinutesZero_ValidationError() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", 0);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは1以上で入力してください");
        }

        @Test
        @DisplayName("正常系：targetMinutesが1の場合、バリデーションが通る")
        void targetMinutesMinValue_NoViolations() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", 1);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：targetMinutesが20001の場合、バリデーションエラー")
        void targetMinutesTooLarge_ValidationError() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", 20001);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは20000以下で入力してください");
        }

        @Test
        @DisplayName("正常系：targetMinutesが20000の場合、バリデーションが通る")
        void targetMinutesMaxValue_NoViolations() {
            // 準備
            request.setMonth("2024-01");
            TestUtil.setField(request, "targetMinutes", 20000);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：複数のバリデーションエラーが発生する場合")
        void multipleValidationErrors() {
            // 準備
            request.setMonth(null);
            TestUtil.setField(request, "targetMinutes", null);

            // 実行
            Set<ConstraintViolation<GoalCreateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(2);
        }
    }

    @Nested
    @DisplayName("trim処理テスト")
    class TrimTests {

        @Test
        @DisplayName("monthの前後空白が除去される")
        void setMonth_Trimmed() {
            // 実行
            request.setMonth("  2024-01  ");

            // 検証
            assertThat(request.getMonth()).isEqualTo("2024-01");
        }

        @Test
        @DisplayName("monthがnullの場合、nullのまま")
        void setMonth_Null_RemainsNull() {
            // 実行
            request.setMonth(null);

            // 検証
            assertThat(request.getMonth()).isNull();
        }
    }
}
