package com.example.studytracker.dto.studyrecord;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 学習記録更新リクエストDTO（部分更新用）
 * nullの項目は更新しない
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordUpdateRequest {

    /**
     * 学習日
     * 任意 / YYYY-MM-DD形式
     */
    private LocalDate date;

    /**
     * 科目名
     * 任意 / 1〜100文字
     */
    @Size(min = 1, max = 100, message = "subjectは1〜100文字で入力してください")
    private String subject;

    /**
     * 学習時間（分）
     * 任意 / 1〜1440分（24時間）
     */
    @Min(value = 1, message = "studyMinutesは1分以上で入力してください")
    @Max(value = 1440, message = "studyMinutesは1440分以内で入力してください")
    private Integer studyMinutes;

    /**
     * メモ
     * 任意 / 0〜1000文字
     */
    @Size(max = 1000, message = "memoは1000文字以内で入力してください")
    private String memo;
}
