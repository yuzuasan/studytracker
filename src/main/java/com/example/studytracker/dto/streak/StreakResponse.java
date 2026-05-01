package com.example.studytracker.dto.streak;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 連続学習日数取得APIのレスポンスDTO
 */
@Getter
@Builder
@AllArgsConstructor
public class StreakResponse {

    /**
     * 現在の連続学習日数
     */
    private Integer currentStreak;
}
