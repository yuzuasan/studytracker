package com.example.studytracker.dto.studyrecord;

import com.example.studytracker.testutil.TestUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StudyRecordUpdateRequestの単体テスト
 */
@DisplayName("StudyRecordUpdateRequest 単体テスト")
class StudyRecordUpdateRequestTest {

    private Validator validator;
    private StudyRecordUpdateRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new StudyRecordUpdateRequest();
    }

    @Nested
    @DisplayName("バリデーションテスト")
    class ValidationTests {

        @Test
        @DisplayName("正常系：すべての項目がnullの場合、バリデーションが通る（部分更新のため全項目任意）")
        void allNull_NoViolations() {
            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：すべての項目が有効な値の場合、バリデーションが通る")
        void validRequest_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "memo", "Javaの基礎を学習");
            TestUtil.setField(request, "tags", List.of("Java", "Spring"));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：dateがnullの場合、バリデーションが通る（任意項目）")
        void nullDate_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", null);
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：subjectがnullの場合、バリデーションが通る（任意項目）")
        void nullSubject_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", null);
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：subjectが空文字の場合、バリデーションエラー")
        void emptySubject_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "");
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("subjectは1〜100文字で入力してください");
        }

        @Test
        @DisplayName("異常系：subjectが空白のみの場合、バリデーションエラー")
        void blankSubject_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            request.setSubject("   ");  // setterを呼び出してtrim処理を適用
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("subjectは1〜100文字で入力してください");
        }

        @Test
        @DisplayName("異常系：subjectが100文字を超える場合、バリデーションエラー")
        void subjectTooLong_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "a".repeat(101));
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("subjectは1〜100文字で入力してください");
        }

        @Test
        @DisplayName("正常系：subjectが100文字の場合、バリデーションが通る")
        void subjectMaxLength_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "a".repeat(100));
            TestUtil.setField(request, "studyMinutes", 60);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("正常系：studyMinutesがnullの場合、バリデーションが通る（任意項目）")
        void nullStudyMinutes_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", null);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：studyMinutesが0の場合、バリデーションエラー")
        void studyMinutesZero_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 0);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("studyMinutesは1分以上で入力してください");
        }

        @Test
        @DisplayName("正常系：studyMinutesが1の場合、バリデーションが通る")
        void studyMinutesMinValue_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 1);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：studyMinutesが1441の場合、バリデーションエラー")
        void studyMinutesTooLarge_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 1441);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("studyMinutesは1440分以内で入力してください");
        }

        @Test
        @DisplayName("正常系：studyMinutesが1440の場合、バリデーションが通る")
        void studyMinutesMaxValue_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 1440);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：memoが1000文字を超える場合、バリデーションエラー")
        void memoTooLong_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "memo", "a".repeat(1001));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("memoは1000文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：memoが1000文字の場合、バリデーションが通る")
        void memoMaxLength_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "memo", "a".repeat(1000));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：tagsが10件を超える場合、バリデーションエラー")
        void tagsTooMany_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("タグは10件以内で入力してください");
        }

        @Test
        @DisplayName("正常系：tagsが10件の場合、バリデーションが通る")
        void tagsMaxCount_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：tagsに空文字が含まれる場合、バリデーションエラー")
        void tagsContainsEmpty_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("Java", ""));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("タグ名は空にできません");
        }

        @Test
        @DisplayName("異常系：tagsに空白のみが含まれる場合、バリデーションエラー")
        void tagsContainsBlank_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("Java", "   "));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("タグ名は空にできません");
        }

        @Test
        @DisplayName("異常系：tagsにnullが含まれる場合、バリデーションエラー")
        void tagsContainsNull_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            List<String> tagsWithNull = new ArrayList<>();
            tagsWithNull.add("Java");
            tagsWithNull.add(null);
            tagsWithNull.add("Spring");
            TestUtil.setField(request, "tags", tagsWithNull);

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("タグ名は空にできません");
        }

        @Test
        @DisplayName("異常系：tagsに50文字を超えるタグ名が含まれる場合、バリデーションエラー")
        void tagsContainsTooLong_ValidationError() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("Java", "a".repeat(51)));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("タグ名は50文字以内で入力してください");
        }

        @Test
        @DisplayName("正常系：tagsに50文字のタグ名が含まれる場合、バリデーションが通る")
        void tagsContainsMaxLength_NoViolations() {
            // 準備
            TestUtil.setField(request, "date", LocalDate.of(2024, 1, 15));
            TestUtil.setField(request, "subject", "Java");
            TestUtil.setField(request, "studyMinutes", 60);
            TestUtil.setField(request, "tags", List.of("Java", "a".repeat(50)));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("異常系：複数のバリデーションエラーが発生する場合")
        void multipleValidationErrors() {
            // 準備
            TestUtil.setField(request, "subject", "");
            TestUtil.setField(request, "studyMinutes", 0);
            TestUtil.setField(request, "memo", "a".repeat(1001));

            // 実行
            Set<ConstraintViolation<StudyRecordUpdateRequest>> violations = validator.validate(request);

            // 検証
            assertThat(violations).hasSize(3);
        }
    }

    @Nested
    @DisplayName("trim処理テスト")
    class TrimTests {

        @Test
        @DisplayName("subjectの前後空白が除去される")
        void setSubject_Trimmed() {
            // 実行
            request.setSubject("  Java  ");

            // 検証
            assertThat(request.getSubject()).isEqualTo("Java");
        }

        @Test
        @DisplayName("subjectがnullの場合、nullのまま")
        void setSubject_Null_RemainsNull() {
            // 実行
            request.setSubject(null);

            // 検証
            assertThat(request.getSubject()).isNull();
        }

        @Test
        @DisplayName("memoの前後空白が除去される")
        void setMemo_Trimmed() {
            // 実行
            request.setMemo("  メモ  ");

            // 検証
            assertThat(request.getMemo()).isEqualTo("メモ");
        }

        @Test
        @DisplayName("memoがnullの場合、nullのまま")
        void setMemo_Null_RemainsNull() {
            // 実行
            request.setMemo(null);

            // 検証
            assertThat(request.getMemo()).isNull();
        }

        @Test
        @DisplayName("正常系：tagsの各タグ名の前後空白が除去される")
        void setTags_Trimmed() {
            // 実行
            request.setTags(List.of("  Java  ", "  Spring  "));

            // 検証
            assertThat(request.getTags()).containsExactly("Java", "Spring");
        }

        @Test
        @DisplayName("正常系：tagsがnullの場合、nullのまま")
        void setTags_Null_RemainsNull() {
            // 実行
            request.setTags(null);

            // 検証
            assertThat(request.getTags()).isNull();
        }

        @Test
        @DisplayName("正常系：tagsにnullが含まれる場合、setterは正常に処理する")
        void setTags_ContainsNull_RemainsNull() {
            // 実行
            List<String> tagsWithNull = new ArrayList<>();
            tagsWithNull.add("Java");
            tagsWithNull.add(null);
            tagsWithNull.add("Spring");
            request.setTags(tagsWithNull);

            // 検証
            assertThat(request.getTags()).containsExactly("Java", null, "Spring");
        }
    }
}
