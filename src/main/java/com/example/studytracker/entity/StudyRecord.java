package com.example.studytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * タグ一覧
     * 学習記録とタグは多対多の関係
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "study_record_tags",
        joinColumns = @JoinColumn(name = "study_record_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

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

    // --- 更新用セッター ---

    /**
     * 学習日を設定する
     *
     * @param studyDate 学習日
     */
    public void setStudyDate(LocalDate studyDate) {
        this.studyDate = studyDate;
    }

    /**
     * 科目名を設定する
     *
     * @param subject 科目名
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 学習時間（分）を設定する
     *
     * @param studyMinutes 学習時間（分）
     */
    public void setStudyMinutes(Integer studyMinutes) {
        this.studyMinutes = studyMinutes;
    }

    /**
     * メモを設定する
     *
     * @param memo メモ
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * タグ一覧を設定する
     *
     * @param tags タグ一覧
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
