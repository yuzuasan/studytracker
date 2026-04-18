package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 学習記録一覧取得レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordListResponse {

    /**
     * 学習記録のリスト
     */
    private List<StudyRecordSummary> records;

    /**
     * 学習記録のサマリー情報（一覧表示用）
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudyRecordSummary {

        /**
         * 学習記録ID
         */
        private Long id;

        /**
         * 学習日
         */
        private LocalDate date;

        /**
         * 科目名
         */
        private String subject;

        /**
         * 学習時間（分）
         */
        private Integer studyMinutes;

        /**
         * タグ名一覧
         */
        private List<String> tags;
    }
}
