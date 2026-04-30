package com.example.studytracker.dto.github;

import lombok.*;

import java.time.LocalDate;

/**
 * GitHubコミット数取得リクエストDTO
 * <p>
 * GET /github/commits のクエリパラメータからバインドされる検索条件を格納する。
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubCommitsRequest {

    /**
     * 開始日
     * <p>
     * 検索対象の開始日（YYYY-MM-DD形式）。任意パラメータ。
     * </p>
     */
    private LocalDate from;

    /**
     * 終了日
     * <p>
     * 検索対象の終了日（YYYY-MM-DD形式）。任意パラメータ。
     * </p>
     */
    private LocalDate to;
}
