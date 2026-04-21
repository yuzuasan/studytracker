package com.example.studytracker.service;

import com.example.studytracker.dto.calendar.CalendarResponse;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学習カレンダー関連のビジネスロジックを担当するServiceクラス
 */
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CurrentUserProvider currentUserProvider;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * 指定された年月の学習カレンダーを取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. year/month未指定時は現在年月を設定
     * 3. 対象期間（月初〜月末）を算出
     * 4. 日付別に学習時間を集計
     * 5. DTOに変換して返却
     *
     * @param year  年（任意、未指定時は現在年）
     * @param month 月（任意、未指定時は現在月）
     * @return 日別学習時間のリスト
     */
    @Transactional(readOnly = true)
    public List<CalendarResponse> getCalendar(Integer year, Integer month) {
        Long userId = currentUserProvider.getUserId();

        // year/monthが未指定の場合は現在年月を使用
        YearMonth targetYearMonth = resolveYearMonth(year, month);

        // 対象期間の初日と末日を算出
        LocalDate from = targetYearMonth.atDay(1);
        LocalDate to = targetYearMonth.atEndOfMonth();

        // 日付別に学習時間を集計
        List<StudyRecordRepository.DailyStudySummary> summaries =
                studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(userId, from, to);

        // DTOに変換
        return summaries.stream()
                .map(summary -> CalendarResponse.builder()
                        .date(summary.getDate())
                        .totalStudyMinutes(summary.getTotalStudyMinutes())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 指定されたyear/monthからYearMonthを解決する
     * 未指定の場合は現在年月を返却する
     *
     * @param year  年（任意）
     * @param month 月（任意）
     * @return 解決されたYearMonth
     */
    private YearMonth resolveYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return YearMonth.now();
        }
        return YearMonth.of(year, month);
    }
}
