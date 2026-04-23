package com.example.studytracker.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 月別統計レスポンスDTO
 * GET /stats/monthly のレスポンスとして使用
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatsResponse {

    /**
     * 年月（yyyy-MM形式）
     */
    private String month;

    /**
     * 学習合計時間（分）
     */
    private Long totalStudyMinutes;
}
