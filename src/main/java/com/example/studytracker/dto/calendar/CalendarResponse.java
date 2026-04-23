package com.example.studytracker.dto.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学習カレンダーレスポンスDTO
 * GET /calendar のレスポンスとして使用
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarResponse {

    /**
     * 日付
     */
    private LocalDate date;

    /**
     * 学習合計時間（分）
     */
    private Long totalStudyMinutes;
}
