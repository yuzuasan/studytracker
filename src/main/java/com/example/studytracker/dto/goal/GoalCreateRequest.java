package com.example.studytracker.dto.goal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 目標作成リクエストDTO
 */
@Getter
public class GoalCreateRequest {

    /**
     * 対象年月
     * 必須 / YYYY-MM形式
     */
    @NotBlank(message = "monthは必須です")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "monthはYYYY-MM形式で入力してください")
    private String month;

    /**
     * 目標学習時間（分）
     * 必須 / 1〜20000
     */
    @NotNull(message = "targetMinutesは必須です")
    @Min(value = 1, message = "targetMinutesは1以上で入力してください")
    @Max(value = 20000, message = "targetMinutesは20000以下で入力してください")
    private Integer targetMinutes;

    /**
     * 対象年月を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param month 対象年月
     */
    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }
}
