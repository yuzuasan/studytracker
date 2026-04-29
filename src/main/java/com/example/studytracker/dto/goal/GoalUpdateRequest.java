package com.example.studytracker.dto.goal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 目標更新リクエストDTO
 */
@Getter
public class GoalUpdateRequest {

    /**
     * 目標学習時間（分）
     * 必須 / 1〜20000
     */
    @NotNull(message = "targetMinutesは必須です")
    @Min(value = 1, message = "targetMinutesは1以上で入力してください")
    @Max(value = 20000, message = "targetMinutesは20000以下で入力してください")
    private Integer targetMinutes;
}
