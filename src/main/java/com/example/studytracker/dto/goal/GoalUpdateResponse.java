package com.example.studytracker.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 目標更新レスポンスDTO
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalUpdateResponse {

    /**
     * 目標ID
     */
    private Long id;
}
