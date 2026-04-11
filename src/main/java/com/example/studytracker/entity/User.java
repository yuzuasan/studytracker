package com.example.studytracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Entity
 * ユーザー情報（認証・GitHub連携）
 */
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_users_username", columnNames = "username")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "github_username", length = 100)
    private String githubUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * リレーション（Phase1では未実装）
     */
    // @OneToMany(mappedBy = "user")
    // private List<StudyRecord> studyRecords;
}
