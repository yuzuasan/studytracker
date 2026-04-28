package com.example.studytracker.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 目標一覧取得レスポンスDTO
 */
@Getter
@Builder
@AllArgsConstructor
public class GoalListResponse {

    /**
     * 目標サマリのリスト
     */
    private List<GoalSummary> goals;

    /**
     * 目標サマリ
     * 目標一覧取得時の各目標情報を表す
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class GoalSummary {
        /**
         * 目標ID
         */
        private Long id;

        /**
         * 対象年月（YYYY-MM形式）
         */
        private String month;

        /**
         * 目標学習時間（分）
         */
        private Integer targetMinutes;

        /**
         * 達成学習時間（分）
         */
        private Integer achievedMinutes;
    }
}
