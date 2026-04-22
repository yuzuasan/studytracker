package com.example.studytracker.service;

import com.example.studytracker.dto.stat.DailyStatsRequest;
import com.example.studytracker.dto.stat.DailyStatsResponse;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学習統計関連のビジネスロジックを担当するServiceクラス
 */
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
        return summaries.stream()
                .map(summary -> DailyStatsResponse.builder()
                        .date(summary.getDate())
                        .totalStudyMinutes(summary.getTotalStudyMinutes())
                        .build())
                .collect(Collectors.toList());
    }
}
