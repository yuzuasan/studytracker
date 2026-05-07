package com.example.studytracker.dto.export;

import com.example.studytracker.testutil.TestUtil;
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
 * ExportCsvRequestの単体テスト
 */
@DisplayName("ExportCsvRequest 単体テスト")
class ExportCsvRequestTest {

    private Validator validator;
    private ExportCsvRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new ExportCsvRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：すべての項目が有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            TestUtil.setField(request, "from", LocalDate.of(2024, 1, 1));
            TestUtil.setField(request, "to", LocalDate.of(2024, 1, 31));

            // 実行
            Set<ConstraintViolation<ExportCsvRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：fromがnullの場合、バリデーションエラー")
        void nullFrom_ValidationError() {
            // 準備
            TestUtil.setField(request, "from", null);
            TestUtil.setField(request, "to", LocalDate.of(2024, 1, 31));

            // 実行
            Set<ConstraintViolation<ExportCsvRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("fromは必須パラメータです");
        }

        @Test
        @DisplayName("異常系：toがnullの場合、バリデーションエラー")
        void nullTo_ValidationError() {
            // 準備
            TestUtil.setField(request, "from", LocalDate.of(2024, 1, 1));
            TestUtil.setField(request, "to", null);

            // 実行
            Set<ConstraintViolation<ExportCsvRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("toは必須パラメータです");
        }

        @Test
        @DisplayName("異常系：両方がnullの場合、バリデーションエラー")
        void bothNull_ValidationError() {
            // 準備
            TestUtil.setField(request, "from", null);
            TestUtil.setField(request, "to", null);

            // 実行
            Set<ConstraintViolation<ExportCsvRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(2);
            assertThat(violations).extracting("message")
                    .contains("fromは必須パラメータです", "toは必須パラメータです");
        }
    }
}
