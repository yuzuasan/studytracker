package com.example.studytracker.service;

import com.example.studytracker.dto.streak.StreakResponse;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * ストリーク関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

    private final CurrentUserProvider currentUserProvider;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * 現在の連続学習日数（ストリーク）を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 学習記録から学習日一覧を取得（DISTINCT date, user_idで絞る）
     * 3. 現在日を取得（LocalDate.now()）
     * 4. ストリーク計算
     *    - currentDate = 今日
     *    - ループ処理：currentDateが学習日一覧に存在する場合はstreak++して1日減算
     *    - 存在しない場合はループ終了
     * 5. レスポンス返却
     *
     * @return 連続学習日数レスポンス
     */
    @Transactional(readOnly = true)
    public StreakResponse getCurrentStreak() {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. 学習日一覧を取得（DISTINCT date, 降順）
        List<LocalDate> studyDates = studyRecordRepository.findDistinctStudyDatesByUserIdOrderByDateDesc(userId);

        // 3. 現在日を取得
        LocalDate currentDate = LocalDate.now();

        // 4. ストリーク計算
        int streak = 0;
        while (true) {
            // currentDateが学習日一覧に存在するかチェック
            if (studyDates.contains(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else {
                // 学習日でない場合はループ終了
                break;
            }
        }

        log.debug("[{}] getCurrentStreak result: streak={}", this.getClass().getSimpleName(), streak);

        // 5. レスポンス返却
        return StreakResponse.builder()
                .currentStreak(streak)
                .build();
    }
}
