package com.example.studytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Goal Entity
 * ユーザーごとの月単位の学習時間目標を管理するエンティティ
 */
@Entity
@Table(
    name = "goals",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_goals_user_month", columnNames = {"user_id", "target_month"})
    }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- リレーション ---
    /**
     * ユーザー
     * 1ユーザーは複数の目標を持つ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- データ ---
    /**
     * 対象年月（YYYY-MM形式）
     * 例: 2026-04
     */
    @Column(name = "target_month", nullable = false, length = 7)
    private String targetMonth;

    /**
     * 目標学習時間（分）
     */
    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    // --- 監査 ---
    /**
     * 作成日時
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 保存前にcreatedAtとupdatedAtを設定する
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新前にupdatedAtを設定する
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
