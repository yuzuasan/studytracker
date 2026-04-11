package com.example.studytracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * バリデーションエラーレスポンス用DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {

    /**
     * ステータス（固定値: error）
     */
    private String status;

    /**
     * エラーメッセージ（固定値: Validation error）
     */
    private String message;

    /**
     * バリデーションエラー詳細リスト
     */
    private List<ValidationError> errors;

    /**
     * バリデーションエラーレスポンスを生成する
     */
    public static ValidationErrorResponse validationError(List<ValidationError> errors) {
        return ValidationErrorResponse.builder()
                .status("error")
                .message("Validation error")
                .errors(errors)
                .build();
    }

    /**
     * バリデーションエラー詳細
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationError {
        /**
         * エラーフィールド名
         */
        private String field;

        /**
         * エラーメッセージ
         */
        private String message;
    }
}
