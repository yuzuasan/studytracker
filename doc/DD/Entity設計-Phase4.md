# Entity設計 - Phase4

## 1. goals（目標）

### 1.1 概要

ユーザーごとの月単位の学習時間目標を管理するエンティティ。
各ユーザーは1ヶ月につき1件の目標のみ登録可能とする。

---

### 1.2 エンティティ定義

| 項目名           | 型             | 必須 | 説明              |
| ------------- | ------------- | -- | --------------- |
| id            | Long          | ○  | 目標ID            |
| userId        | Long          | ○  | ユーザーID          |
| targetMonth   | String        | ○  | 対象年月（YYYY-MM形式） |
| targetMinutes | Integer       | ○  | 目標学習時間（分）       |
| createdAt     | LocalDateTime | ○  | 作成日時            |
| updatedAt     | LocalDateTime | ○  | 更新日時            |

---

### 1.3 制約

#### ■ 主キー

* id

#### ■ 外部キー

* user_id → users.id

#### ■ ユニーク制約

* user_id + target_month

（1ユーザーは同一月に1つの目標のみ登録可能）

---

### 1.4 他エンティティとの関係

| エンティティ | 関係        |
| ------ | --------- |
| User   | ManyToOne |

※ StudyRecordとは直接のリレーションは持たない
（達成状況は動的に計算するため）

---

### 1.5 補足事項

#### ■ targetMonthについて

* 形式：YYYY-MM（例：2026-04）
* VARCHAR(7)で保持する
* アプリケーション側でフォーマットチェックを行う

---

#### ■ 達成状況について

以下の値は本エンティティでは保持しない：

* 実績学習時間
* 達成率

これらはStudyRecordから動的に算出する。

---

#### ■ 科目（Subject）との関係

本エンティティは科目単位の目標は扱わず、
ユーザー全体の学習時間目標のみを管理する。

---

---

# JPAエンティティ実装例

```java
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

    // ユーザー
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 対象年月（YYYY-MM）
    @Column(name = "target_month", nullable = false, length = 7)
    private String targetMonth;

    // 目標学習時間（分）
    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    // 作成日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```
