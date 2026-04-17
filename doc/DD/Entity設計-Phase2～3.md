# ■ Entity設計（Phase2 / Phase3）

## ■ 1. Tag

### ■ 概要

ユーザーごとのタグを管理するエンティティ

---

### ■ クラス定義

```java
@Entity
@Table(name = "tags",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ユーザー
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * タグ名
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

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
     * 学習記録との関連（多対多）
     */
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<StudyRecord> studyRecords;
}
```

---

### ■ 設計ポイント

* `user_id + name` で一意制約
* StudyRecord と **双方向 ManyToMany**

---

## ■ 2. StudyRecord（拡張）

### ■ 変更点

タグとの関連を追加

---

### ■ 追加フィールド

```java
/**
 * タグ一覧
 */
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "study_record_tags",
    joinColumns = @JoinColumn(name = "study_record_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags;
```

---

### ■ 設計ポイント

* 中間テーブルを使用した ManyToMany
* 双方向関連（Tag側にも mappedBy）
* カスケードは基本付けない（意図しない削除防止）

---

## ■ 3. StudyRecordTag（※採用しない方針）

### ■ 方針

今回は **Entityとしては定義しない**

---

### ■ 理由

* 単純な中間テーブルのため
* 追加属性（例：付与日時など）がない
* JPAのManyToManyで十分

---

### ■ 将来拡張

以下の場合はEntity化を検討：

* タグ付与日時を持たせたい
* 並び順を持たせたい
* 論理削除したい

---

## ■ 4. User（変更なし）

Phase1設計をそのまま利用

---

# ■ リレーション構成まとめ

```
User (1) ─── (N) Tag

StudyRecord (N) ─── (N) Tag
        ↓
 study_record_tags（中間テーブル）
```

---

# ■ フェッチ戦略（重要）

### ■ tags

```java
@ManyToMany(fetch = FetchType.LAZY)
```

* デフォルトLAZY（必須）
* 一覧APIでN+1問題に注意

---

# ■ 注意点（実装時）

## ■ ① N+1問題対策

* `@EntityGraph`
* `JOIN FETCH`
* DTOクエリ

---

## ■ ② タグ更新処理

* 差分更新が必要
* 全削除→再登録でもOK（Phase2は簡易実装推奨）
