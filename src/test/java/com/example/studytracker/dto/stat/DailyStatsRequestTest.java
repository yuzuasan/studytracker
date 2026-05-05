package com.example.studytracker.dto.stat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DailyStatsRequestの単体テスト
 */
@DisplayName("DailyStatsRequest 単体テスト")
class DailyStatsRequestTest {

    private Validator validator;
    private DailyStatsRequest dailyStatsRequest;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        dailyStatsRequest = new DailyStatsRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：fromとtoが有効な日付の場合、バリデーションが通る")
        void validDates_NoViolations() {
            // 準備
            dailyStatsRequest.setFrom(LocalDate.of(2024, 1, 1));
            dailyStatsRequest.setTo(LocalDate.of(2024, 1, 31));

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：fromがnullの場合、バリデーションエラー")
        void nullFrom_ValidationError() {
            // 準備
            dailyStatsRequest.setFrom(null);
            dailyStatsRequest.setTo(LocalDate.of(2024, 1, 31));

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("fromは必須パラメータです");
        }

        @Test
        @DisplayName("異常系：toがnullの場合、バリデーションエラー")
        void nullTo_ValidationError() {
            // 準備
            dailyStatsRequest.setFrom(LocalDate.of(2024, 1, 1));
            dailyStatsRequest.setTo(null);

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("toは必須パラメータです");
        }

        @Test
        @DisplayName("異常系：fromとtoが両方nullの場合、バリデーションエラー")
        void bothNull_ValidationError() {
            // 準備
            dailyStatsRequest.setFrom(null);
            dailyStatsRequest.setTo(null);

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).hasSize(2);
        }

        @Test
        @DisplayName("正常系：fromとtoが同じ日の場合、バリデーションが通る")
        void sameDate_NoViolations() {
            // 準備
            LocalDate sameDate = LocalDate.of(2024, 1, 15);
            dailyStatsRequest.setFrom(sameDate);
            dailyStatsRequest.setTo(sameDate);

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：fromがtoより前の日付の場合、バリデーションが通る")
        void fromBeforeTo_NoViolations() {
            // 準備
            dailyStatsRequest.setFrom(LocalDate.of(2024, 1, 1));
            dailyStatsRequest.setTo(LocalDate.of(2024, 1, 31));

            // 実行
            Set<ConstraintViolation<DailyStatsRequest>> violations = validator.validate(dailyStatsRequest);

            // 検証
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Builderテスト")
    class BuilderTests {

        @Test
        @DisplayName("正常系：Builderでインスタンスを作成できる")
        void builder_CreatesInstance() {
            // 実行
            DailyStatsRequest request = DailyStatsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            // 検証
            assertThat(request).isNotNull();
            assertThat(request.getFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
            assertThat(request.getTo()).isEqualTo(LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("正常系：Builderで全項目nullのインスタンスを作成できる")
        void builder_WithNulls_CreatesInstance() {
            // 実行
            DailyStatsRequest request = DailyStatsRequest.builder()
                    .from(null)
                    .to(null)
                    .build();

            // 検証
            assertThat(request).isNotNull();
            assertThat(request.getFrom()).isNull();
            assertThat(request.getTo()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter/Setterテスト")
    class GetterSetterTests {

        @Test
        @DisplayName("正常系：fromのgetter/setterが正しく動作する")
        void fromGetterSetter_WorksCorrectly() {
            // 実行
            dailyStatsRequest.setFrom(LocalDate.of(2024, 1, 1));

            // 検証
            assertThat(dailyStatsRequest.getFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("正常系：toのgetter/setterが正しく動作する")
        void toGetterSetter_WorksCorrectly() {
            // 実行
            dailyStatsRequest.setTo(LocalDate.of(2024, 1, 31));

            // 検証
            assertThat(dailyStatsRequest.getTo()).isEqualTo(LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("正常系：fromにnullを設定できる")
        void fromSetterNull_WorksCorrectly() {
            // 実行
            dailyStatsRequest.setFrom(null);

            // 検証
            assertThat(dailyStatsRequest.getFrom()).isNull();
        }

        @Test
        @DisplayName("正常系：toにnullを設定できる")
        void toSetterNull_WorksCorrectly() {
            // 実行
            dailyStatsRequest.setTo(null);

            // 検証
            assertThat(dailyStatsRequest.getTo()).isNull();
        }
    }
}
