package com.example.studytracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * APIエラーレスポンス用DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * ステータス（固定値: error）
     */
    private String status;

    /**
     * エラーメッセージ
     */
    private String message;

    /**
     * エラーレスポンスを生成する
     */
    public static ErrorResponse error(String message) {
        return ErrorResponse.builder()
                .status("error")
                .message(message)
                .build();
    }
}
