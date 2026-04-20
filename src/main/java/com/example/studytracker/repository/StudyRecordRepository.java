package com.example.studytracker.repository;

import com.example.studytracker.entity.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

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
}
