package com.example.studytracker.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 目標削除レスポンスDTO
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalDeleteResponse {

    /**
     * 削除メッセージ
     */
    private String message;
}
