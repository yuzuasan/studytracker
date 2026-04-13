package com.example.studytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StudyRecord Entity
 * 学習記録のコアデータ
 */
@Entity
@Table(name = "study_records",
       indexes = @Index(name = "idx_study_user_date",
                        columnList = "user_id, study_date"))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- リレーション ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // --- データ ---
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(name = "study_minutes", nullable = false)
    private Integer studyMinutes;

    @Column(length = 1000)
    private String memo;

    // --- 監査 ---
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

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
