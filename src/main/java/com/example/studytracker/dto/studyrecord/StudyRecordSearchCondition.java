package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 学習記録検索条件DTO
 * <p>
 * GET /study-records のクエリパラメータからバインドされる検索条件を格納する。
 * すべてのフィールドは任意（null許容）であり、nullの場合は該当条件を適用しない。
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordSearchCondition {

    /**
     * 開始日
     * <p>
     * 検索対象の開始日（YYYY-MM-DD形式）。
     * 指定した場合、study_date >= from の条件が適用される。
     * </p>
     */
    private LocalDate from;

    /**
     * 終了日
     * <p>
     * 検索対象の終了日（YYYY-MM-DD形式）。
     * 指定した場合、study_date <= to の条件が適用される。
     * </p>
     */
    private LocalDate to;

    /**
     * タグ名
     * <p>
     * タグ名による完全一致検索。
     * 指定した場合、tags.name = tag の条件が適用される（INNER JOIN）。
     * </p>
     */
    private String tag;

    /**
     * メモ検索キーワード
     * <p>
     * メモ内容による部分一致検索。
     * 指定した場合、memo LIKE %keyword% の条件が適用される。
     * </p>
     */
    private String keyword;
}
