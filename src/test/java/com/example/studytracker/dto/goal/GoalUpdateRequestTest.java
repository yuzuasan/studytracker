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
 * GoalUpdateRequestの単体テスト
 */
@DisplayName("GoalUpdateRequest 単体テスト")
class GoalUpdateRequestTest {

    private Validator validator;
    private GoalUpdateRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new GoalUpdateRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：targetMinutesが有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            TestUtil.setField(request, "targetMinutes", 3000);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：targetMinutesがnullの場合、バリデーションエラー")
        void nullTargetMinutes_ValidationError() {
            // 準備
            TestUtil.setField(request, "targetMinutes", null);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは必須です");
        }

        @Test
        @DisplayName("異常系：targetMinutesが0の場合、バリデーションエラー")
        void targetMinutesZero_ValidationError() {
            // 準備
            TestUtil.setField(request, "targetMinutes", 0);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは1以上で入力してください");
        }

        @Test
        @DisplayName("正常系：targetMinutesが1の場合、バリデーションが通る")
        void targetMinutesMinValue_NoViolations() {
            // 準備
            TestUtil.setField(request, "targetMinutes", 1);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：targetMinutesが20001の場合、バリデーションエラー")
        void targetMinutesTooLarge_ValidationError() {
            // 準備
            TestUtil.setField(request, "targetMinutes", 20001);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("targetMinutesは20000以下で入力してください");
        }

        @Test
        @DisplayName("正常系：targetMinutesが20000の場合、バリデーションが通る")
        void targetMinutesMaxValue_NoViolations() {
            // 準備
            TestUtil.setField(request, "targetMinutes", 20000);

            // 実行
            Set<ConstraintViolation<GoalUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }
    }
}
