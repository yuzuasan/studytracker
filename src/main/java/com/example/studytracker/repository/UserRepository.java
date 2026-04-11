package com.example.studytracker.repository;

import com.example.studytracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository
 * User Entityのデータアクセスを担当
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * usernameでユーザーを検索する
     * 重複チェックやログイン時に使用
     *
     * @param username ユーザー名
     * @return ユーザー（存在しない場合はEmpty）
     */
    Optional<User> findByUsername(String username);
}
