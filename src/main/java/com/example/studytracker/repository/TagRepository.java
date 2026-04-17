package com.example.studytracker.repository;

import com.example.studytracker.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TagRepository
 * Tag Entityのデータアクセスを担当
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * ユーザーIDとタグ名でタグを検索する
     * タグ作成時の重複チェックに使用
     *
     * @param userId ユーザーID
     * @param name タグ名
     * @return タグ（存在しない場合はEmpty）
     */
    Optional<Tag> findByUserIdAndName(Long userId, String name);

    /**
     * ユーザーIDでタグ一覧を検索する（タグ名昇順）
     * タグ一覧取得時に使用
     *
     * @param userId ユーザーID
     * @return タグリスト（タグ名昇順）
     */
    List<Tag> findByUserIdOrderByNameAsc(Long userId);
}
