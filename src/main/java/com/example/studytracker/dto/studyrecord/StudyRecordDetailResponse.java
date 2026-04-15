package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 学習記録詳細取得レスポンスDTO
 * GET /study-records/{id} のレスポンスとして使用
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordDetailResponse {
    
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
     * メモ
     */
    private String memo;
    
    /**
     * タグリスト
     */
    private List<String> tags;
}
