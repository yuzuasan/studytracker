package com.example.studytracker.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 日別統計レスポンスDTO
 * GET /stats/daily のレスポンスとして使用
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatsResponse {

    /**
     * 日付
     */
    private LocalDate date;

    /**
     * 学習合計時間（分）
     */
    private Long totalStudyMinutes;
}
