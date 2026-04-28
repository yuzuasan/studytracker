package com.example.studytracker.repository;

import com.example.studytracker.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * GoalRepository
 * Goal Entityのデータアクセスを担当
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * ユーザーIDと対象年月で目標を検索する
     * 目標作成時の重複チェックに使用
     *
     * @param userId ユーザーID
     * @param targetMonth 対象年月（YYYY-MM形式）
     * @return 目標（存在しない場合はEmpty）
     */
    Optional<Goal> findByUserIdAndTargetMonth(Long userId, String targetMonth);

    /**
     * IDとユーザーIDで目標を検索する
     * 目標更新・削除時の存在チェック・所有権確認に使用
     *
     * @param id 目標ID
     * @param userId ユーザーID
     * @return 目標（存在しない場合はEmpty）
     */
    Optional<Goal> findByIdAndUserId(Long id, Long userId);

    /**
     * ユーザーIDで目標一覧を検索する（対象年月降順）
     * 目標一覧取得時に使用
     *
     * @param userId ユーザーID
     * @return 目標リスト（対象年月降順）
     */
    List<Goal> findByUserIdOrderByTargetMonthDesc(Long userId);
}
