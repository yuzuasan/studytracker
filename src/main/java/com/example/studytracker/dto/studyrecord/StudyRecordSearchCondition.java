package com.example.studytracker.dto.studyrecord;

import lombok.Getter;
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

    /**
     * タグ名を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param tag タグ名
     */
    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    /**
     * メモ検索キーワードを設定する
     * バリデーション前に前後空白を除去する
     *
     * @param keyword メモ検索キーワード
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }
}
