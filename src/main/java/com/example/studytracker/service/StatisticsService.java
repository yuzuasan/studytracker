package com.example.studytracker.service;

import com.example.studytracker.dto.stat.DailyStatsRequest;
import com.example.studytracker.dto.stat.DailyStatsResponse;
import com.example.studytracker.dto.stat.MonthlyStatsResponse;
import com.example.studytracker.dto.stat.SubjectStatsResponse;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学習統計関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CurrentUserProvider currentUserProvider;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * 指定期間の日別学習時間を集計して取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 対象期間の学習記録を日付別に集計（DB側でGROUP BY）
     * 3. DTOに変換して返却
     *
     * @param request 日別統計リクエスト
     * @return 日別統計のリスト
     */
    @Transactional(readOnly = true)
    public List<DailyStatsResponse> getDailyStats(DailyStatsRequest request) {
        Long userId = currentUserProvider.getUserId();
        LocalDate from = request.getFrom();
        LocalDate to = request.getTo();

        // 日付別に学習時間を集計（DB側でGROUP BY/SUM）
        List<StudyRecordRepository.DailyStudySummary> summaries =
                studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(userId, from, to);

        // DTOに変換
        List<DailyStatsResponse> result = summaries.stream()
                .map(summary -> DailyStatsResponse.builder()
                        .date(summary.getDate())
                        .totalStudyMinutes(summary.getTotalStudyMinutes())
                        .build())
                .collect(Collectors.toList());

        log.debug("[{}] getDailyStats result: count={}", this.getClass().getSimpleName(), result.size());
        return result;
    }

    /**
     * 月別学習時間を集計して取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 学習記録を年月別に集計（DB側でGROUP BY）
     * 3. DTOに変換して返却
     *
     * @return 月別統計のリスト
     */
    @Transactional(readOnly = true)
    public List<MonthlyStatsResponse> getMonthlyStats() {
        Long userId = currentUserProvider.getUserId();

        // 年月別に学習時間を集計（DB側でGROUP BY/SUM）
        List<StudyRecordRepository.MonthlyStudySummary> summaries =
                studyRecordRepository.findMonthlyStudySummaryByUserId(userId);

        // DTOに変換
        List<MonthlyStatsResponse> result = summaries.stream()
                .map(summary -> MonthlyStatsResponse.builder()
                        .month(summary.getMonth())
                        .totalStudyMinutes(summary.getTotalStudyMinutes())
                        .build())
                .collect(Collectors.toList());

        log.debug("[{}] getMonthlyStats result: count={}", this.getClass().getSimpleName(), result.size());
        return result;
    }

    /**
     * 科目別学習時間を集計して取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 学習記録を科目別に集計（DB側でGROUP BY）
     * 3. DTOに変換して返却
     *
     * @return 科目別統計のリスト
     */
    @Transactional(readOnly = true)
    public List<SubjectStatsResponse> getSubjectStats() {
        Long userId = currentUserProvider.getUserId();

        // 科目別に学習時間を集計（DB側でGROUP BY/SUM）
        List<StudyRecordRepository.SubjectStudySummary> summaries =
                studyRecordRepository.findSubjectStudySummaryByUserId(userId);

        // DTOに変換
        List<SubjectStatsResponse> result = summaries.stream()
                .map(summary -> SubjectStatsResponse.builder()
                        .subject(summary.getSubject())
                        .totalStudyMinutes(summary.getTotalStudyMinutes())
                        .build())
                .collect(Collectors.toList());

        log.debug("[{}] getSubjectStats result: count={}", this.getClass().getSimpleName(), result.size());
        return result;
    }
}
