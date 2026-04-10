# ■ ① Entity設計（Phase1）

---

## ■ 対象エンティティ

Phase1では以下のみ扱う：

* User
* StudyRecord

---

# ■ 1. User Entity

---

## ■ テーブル対応

users

---

## ■ フィールド

```java
private Long id;
private String username;
private String password;
private String githubUsername;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

---

## ■ アノテーション

```java
@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_users_username", columnNames = "username")
       })
```

---

## ■ 実装例

```java
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
}
```

---

## ■ リレーション

```java
@OneToMany(mappedBy = "user")
private List<StudyRecord> studyRecords;
```

※ 双方向にするかは任意（Phase1ではなくてもOK）

---

---

# ■ 2. StudyRecord Entity

---

## ■ テーブル対応

study_records

---

## ■ フィールド

```java
private Long id;
private User user;
private LocalDate studyDate;
private String subject;
private Integer studyMinutes;
private String memo;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
```

---

## ■ アノテーション

```java
@Entity
@Table(name = "study_records")
```

---

## ■ 実装例

```java
@Entity
@Table(name = "study_records")
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
}
```

---

## ■ リレーション

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```

---

---

# ■ 3. リレーション設計まとめ

| 関係                     | 内容         |
| ---------------------- | ---------- |
| User 1 : N StudyRecord | 1ユーザーに複数記録 |

---

# ■ 4. 設計方針

---

## ■ Lombok

👉 **使用する**

使用アノテーション：

* @Getter
* @NoArgsConstructor
* @AllArgsConstructor
* @Builder

---

## ■ equals / hashCode

👉 **Phase1では未実装（OK）**

理由：

* 複雑化を避ける
* JPAでは不用意な実装がバグ要因

---

## ■ fetch戦略

| 関係        | 設定   |
| --------- | ---- |
| ManyToOne | LAZY |

---

## ■ created_at / updated_at

👉 自動設定はPhase2以降でもOK
（@PrePersist / @PreUpdate でも可）
