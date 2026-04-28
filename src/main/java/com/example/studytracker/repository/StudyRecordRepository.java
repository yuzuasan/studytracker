package com.example.studytracker.repository;

import com.example.studytracker.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * StudyRecordRepository
 * StudyRecord Entityのデータアクセスを担当
 *
 * JpaSpecificationExecutorを継承し、動的クエリ（検索条件による絞り込み）に対応
 */
@Repository
public interface StudyRecordRepository
        extends JpaRepository<StudyRecord, Long>,
        JpaSpecificationExecutor<StudyRecord> {

    /**
     * IDとユーザーIDで学習記録を検索する
     * 認可チェック（自分のデータのみアクセス）に使用
     *
     * @param id 学習記録ID
     * @param userId ユーザーID
     * @return 学習記録（存在しない場合はEmpty）
     */
    Optional<StudyRecord> findByIdAndUserId(Long id, Long userId);

    /**
     * 日付別に学習時間を集計する
     * カレンダー表示用に使用
     *
     * @param userId ユーザーID
     * @param from 開始日
     * @param to 終了日
     * @return 日別学習時間のリスト（日付昇順）
     */
    @Query("SELECT sr.studyDate AS date, SUM(sr.studyMinutes) AS totalStudyMinutes " +
           "FROM StudyRecord sr " +
           "WHERE sr.user.id = :userId " +
           "AND sr.studyDate BETWEEN :from AND :to " +
           "GROUP BY sr.studyDate " +
           "ORDER BY sr.studyDate ASC")
    List<DailyStudySummary> findDailyStudySummaryByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    /**
     * 月別に学習時間を集計する
     * 月別統計表示用に使用
     *
     * 【注意】本クエリはH2データベース固有の関数（FORMATDATETIME）を使用しています。
     * 本番環境（MySQL/PostgreSQL等）への移行時は、データベースに応じた関数への変更が必要です。
     *
     * @param userId ユーザーID
     * @return 月別学習時間のリスト（年月昇順）
     */
    @Query("SELECT FUNCTION('FORMATDATETIME', sr.studyDate, 'yyyy-MM') AS month, " +
           "SUM(sr.studyMinutes) AS totalStudyMinutes " +
           "FROM StudyRecord sr " +
           "WHERE sr.user.id = :userId " +
           "GROUP BY FUNCTION('FORMATDATETIME', sr.studyDate, 'yyyy-MM') " +
           "ORDER BY FUNCTION('FORMATDATETIME', sr.studyDate, 'yyyy-MM') ASC")
    List<MonthlyStudySummary> findMonthlyStudySummaryByUserId(@Param("userId") Long userId);

    /**
     * 科目別に学習時間を集計する
     * 科目別統計表示用に使用
     *
     * @param userId ユーザーID
     * @return 科目別学習時間のリスト（学習時間降順）
     */
    @Query("SELECT sr.subject AS subject, " +
            "SUM(sr.studyMinutes) AS totalStudyMinutes " +
            "FROM StudyRecord sr " +
            "WHERE sr.user.id = :userId " +
            "GROUP BY sr.subject " +
            "ORDER BY SUM(sr.studyMinutes) DESC")
    List<SubjectStudySummary> findSubjectStudySummaryByUserId(@Param("userId") Long userId);

    /**
     * 指定期間の学習時間を集計する
     * 目標達成状況の計算に使用
     *
     * @param userId ユーザーID
     * @param from 開始日
     * @param to 終了日
     * @return 学習時間の合計（分）、該当データがない場合はnull
     */
    @Query("SELECT SUM(sr.studyMinutes) " +
           "FROM StudyRecord sr " +
           "WHERE sr.user.id = :userId " +
           "AND sr.studyDate BETWEEN :from AND :to")
    Long findTotalStudyMinutesByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    /**
     * 日付別集計結果のプロジェクションインターフェース
     */
    interface DailyStudySummary {
        /**
         * 日付を取得する
         *
         * @return 日付
         */
        LocalDate getDate();

        /**
         * 学習合計時間（分）を取得する
         *
         * @return 学習合計時間（分）
         */
        Long getTotalStudyMinutes();
    }

    /**
     * 月別集計結果のプロジェクションインターフェース
     */
    interface MonthlyStudySummary {
        /**
         * 年月（yyyy-MM形式）を取得する
         *
         * @return 年月
         */
        String getMonth();

        /**
         * 学習合計時間（分）を取得する
         *
         * @return 学習合計時間（分）
         */
        Long getTotalStudyMinutes();
    }

    /**
     * 科目別集計結果のプロジェクションインターフェース
     */
    interface SubjectStudySummary {
        /**
         * 科目名を取得する
         *
         * @return 科目名
         */
        String getSubject();

        /**
         * 学習合計時間（分）を取得する
         *
         * @return 学習合計時間（分）
         */
        Long getTotalStudyMinutes();
    }
}
