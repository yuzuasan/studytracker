package com.example.studytracker.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 目標作成レスポンスDTO
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalCreateResponse {

    /**
     * 目標ID
     */
    private Long id;
}
