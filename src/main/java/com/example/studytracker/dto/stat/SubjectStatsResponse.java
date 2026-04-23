package com.example.studytracker.dto.stat;

import lombok.Builder;

/**
 * 科目別統計レスポンスDTO
 */
@Builder
public class SubjectStatsResponse {

    /**
     * 科目名
     */
    private String subject;

    /**
     * 学習合計時間（分）
     */
    private Integer totalStudyMinutes;
}
