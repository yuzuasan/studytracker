package com.example.studytracker.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 科目別統計レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
