package com.example.studytracker.dto.export;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * CSVエクスポートリクエストDTO
 * <p>
 * GET /export/csv のクエリパラメータからバインドされる検索条件を格納する。
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportCsvRequest {

    /**
     * 開始日
     * <p>
     * エクスポート対象の開始日（YYYY-MM-DD形式）。必須パラメータ。
     * </p>
     */
    @NotNull(message = "fromは必須パラメータです")
    private LocalDate from;

    /**
     * 終了日
     * <p>
     * エクスポート対象の終了日（YYYY-MM-DD形式）。必須パラメータ。
     * </p>
     */
    @NotNull(message = "toは必須パラメータです")
    private LocalDate to;
}
