package com.example.studytracker.service;

import com.example.studytracker.dto.goal.GoalCreateRequest;
import com.example.studytracker.dto.goal.GoalCreateResponse;
import com.example.studytracker.dto.goal.GoalListResponse;
import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.GoalRepository;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import com.example.studytracker.util.DateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 目標管理関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final DateValidator dateValidator;

    /**
     * 目標を作成する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 同一ユーザー・同一月の目標存在チェック
     * 3. 存在する場合はエラー（409）を返却
     * 4. Goalエンティティを作成
     * 5. 保存
     * 6. レスポンス返却（id）
     *
     * @param request 目標作成リクエスト
     * @return 目標作成レスポンス
     * @throws ConflictException 同一月の目標が既に存在する場合
     * @throws ResourceNotFoundException ユーザーが存在しない場合
     */
    @Transactional
    public GoalCreateResponse create(GoalCreateRequest request) {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 対象年月のバリデーション（年・月の有効範囲チェック）
        String[] yearMonth = request.getMonth().split("-");
        int year = Integer.parseInt(yearMonth[0]);
        int month = Integer.parseInt(yearMonth[1]);
        dateValidator.validateYearMonth(year, month);

        // 2. 同一ユーザー・同一月の目標存在チェック
        if (goalRepository.findByUserIdAndTargetMonth(userId, request.getMonth()).isPresent()) {
            throw new ConflictException("指定された月の目標が既に存在します");
        }

        // ユーザー取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 4. Goalエンティティを作成
        Goal goal = Goal.builder()
                .user(user)
                .targetMonth(request.getMonth())
                .targetMinutes(request.getTargetMinutes())
                .build();

        // 5. 保存（createdAt/updatedAtは@PrePersistで自動設定）
        Goal saved = goalRepository.save(goal);

        log.debug("[{}] create result: id={}", this.getClass().getSimpleName(), saved.getId());

        // 6. レスポンス返却
        return GoalCreateResponse.builder()
                .id(saved.getId())
                .build();
    }

    /**
     * 目標一覧を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. 目標一覧を取得（user_idで絞る、month DESC）
     * 3. 各目標について以下を実施
     *    - monthから対象期間を算出（開始日：YYYY-MM-01、終了日：月末）
     *    - StudyRecordから対象期間の学習時間を集計（SUM）
     *    - achievedMinutesとして設定
     * 4. DTOに変換して返却
     *
     * @return 目標一覧レスポンス
     */
    @Transactional(readOnly = true)
    public GoalListResponse getAllGoals() {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. 目標一覧を取得（対象年月降順）
        List<Goal> goals = goalRepository.findByUserIdOrderByTargetMonthDesc(userId);

        // 3. 各目標について達成状況を計算
        List<GoalListResponse.GoalSummary> goalSummaries = goals.stream()
                .map(goal -> {
                    // monthから対象期間を算出
                    String[] yearMonth = goal.getTargetMonth().split("-");
                    int year = Integer.parseInt(yearMonth[0]);
                    int month = Integer.parseInt(yearMonth[1]);

                    // 開始日：YYYY-MM-01
                    LocalDate fromDate = LocalDate.of(year, month, 1);
                    // 終了日：月末
                    LocalDate toDate = YearMonth.of(year, month).atEndOfMonth();

                    // StudyRecordから対象期間の学習時間を集計
                    Long totalMinutes = studyRecordRepository.findTotalStudyMinutesByUserIdAndDateRange(
                            userId, fromDate, toDate);

                    // 該当データがない場合は0として扱う
                    Integer achievedMinutes = totalMinutes != null ? totalMinutes.intValue() : 0;

                    // DTOに変換
                    return GoalListResponse.GoalSummary.builder()
                            .id(goal.getId())
                            .month(goal.getTargetMonth())
                            .targetMinutes(goal.getTargetMinutes())
                            .achievedMinutes(achievedMinutes)
                            .build();
                })
                .toList();

        log.debug("[{}] getAllGoals result: count={}", this.getClass().getSimpleName(), goalSummaries.size());

        // 4. レスポンス返却
        return GoalListResponse.builder()
                .goals(goalSummaries)
                .build();
    }
}
