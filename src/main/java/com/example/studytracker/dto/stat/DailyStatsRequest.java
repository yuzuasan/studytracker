package com.example.studytracker.dto.stat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 日別統計リクエストDTO
 * <p>
 * GET /stats/daily のクエリパラメータからバインドされる検索条件を格納する。
 * from と to は必須パラメータである。
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStatsRequest {

    /**
     * 開始日
     * <p>
     * 検索対象の開始日（YYYY-MM-DD形式）。必須パラメータ。
     * </p>
     */
    @NotNull(message = "fromは必須パラメータです")
    private LocalDate from;

    /**
     * 終了日
     * <p>
     * 検索対象の終了日（YYYY-MM-DD形式）。必須パラメータ。
     * </p>
     */
    @NotNull(message = "toは必須パラメータです")
    private LocalDate to;
}
