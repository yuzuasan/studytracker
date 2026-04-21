package com.example.studytracker.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API正常レスポンス用DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuccessResponse<T> {

    /**
     * ステータス（固定値: success）
     */
    private String status;

    /**
     * レスポンスデータ
     */
    private T data;

    /**
     * 成功レスポンスを生成する
     */
    public static <T> SuccessResponse<T> success(T data) {
        return SuccessResponse.<T>builder()
                .status("success")
                .data(data)
                .build();
    }
}
