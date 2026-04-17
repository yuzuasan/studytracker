# ■ DTO設計（Phase2 / Phase3）

---

# ■ 1. タグ管理API

---

## ■ 1.1 タグ作成

### ■ クラス名

`TagCreateRequest`

```java
/**
 * タグ作成リクエストDTO
 */
public class TagCreateRequest {

    /**
     * タグ名
     */
    private String name;
}
```

---

### ■ クラス名

`TagCreateResponse`

```java
/**
 * タグ作成レスポンスDTO
 */
public class TagCreateResponse {

    /**
     * タグID
     */
    private Long id;
}
```

---

## ■ 1.2 タグ一覧取得

### ■ クラス名

`TagListResponse`

```java
/**
 * タグ一覧レスポンスDTO
 */
public class TagListResponse {

    private List<TagResponse> tags;
}
```

---

### ■ クラス名

`TagResponse`

```java
/**
 * タグDTO
 */
public class TagResponse {

    private Long id;

    private String name;
}
```

---

## ■ 1.3 タグ削除

### ■ クラス名

`TagDeleteResponse`

```java
/**
 * タグ削除レスポンスDTO
 */
public class TagDeleteResponse {

    private String message;
}
```

---

# ■ 2. 学習記録API（Phase1拡張）

---

## ■ 2.1 学習記録作成

### ■ クラス名

`StudyRecordCreateRequest`

```java
/**
 * 学習記録作成リクエストDTO
 */
public class StudyRecordCreateRequest {

    private LocalDate date;

    private String subject;

    private Integer studyMinutes;

    private String memo;

    /**
     * タグ名一覧
     */
    private List<String> tags;
}
```

---

### ■ クラス名

`StudyRecordCreateResponse`

```java
/**
 * 学習記録作成レスポンスDTO
 */
public class StudyRecordCreateResponse {

    private Long id;
}
```

---

## ■ 2.2 学習記録更新

### ■ クラス名

`StudyRecordUpdateRequest`

```java
/**
 * 学習記録更新リクエストDTO
 */
public class StudyRecordUpdateRequest {

    private LocalDate date;

    private String subject;

    private Integer studyMinutes;

    private String memo;

    /**
     * タグ名一覧
     */
    private List<String> tags;
}
```

---

## ■ 2.3 学習記録一覧取得

### ■ クラス名

`StudyRecordListResponse`

```java
/**
 * 学習記録一覧レスポンスDTO
 */
public class StudyRecordListResponse {

    private List<StudyRecordSummary> records;
}
```

---

### ■ クラス名

`StudyRecordSummary`

```java
/**
 * 学習記録サマリDTO
 */
public class StudyRecordSummary {

    private Long id;

    private LocalDate date;

    private String subject;

    private Integer studyMinutes;

    /**
     * タグ一覧
     */
    private List<String> tags;
}
```

---

## ■ 2.4 学習記録詳細取得

### ■ クラス名

`StudyRecordDetailResponse`

```java
/**
 * 学習記録詳細レスポンスDTO
 */
public class StudyRecordDetailResponse {

    private Long id;

    private LocalDate date;

    private String subject;

    private Integer studyMinutes;

    private String memo;

    /**
     * タグ一覧
     */
    private List<String> tags;
}
```

---

## ■ 2.5 学習記録検索条件

### ■ クラス名

`StudyRecordSearchCondition`

```java
/**
 * 学習記録検索条件DTO
 */
public class StudyRecordSearchCondition {

    /**
     * 開始日
     */
    private LocalDate from;

    /**
     * 終了日
     */
    private LocalDate to;

    /**
     * タグ名
     */
    private String tag;

    /**
     * メモ検索キーワード
     */
    private String keyword;
}
```

---

# ■ 3. カレンダーAPI

---

## ■ 3.1 学習カレンダー取得

### ■ クラス名

`CalendarResponse`

```java id="3q9m1z"
/**
 * 学習カレンダーレスポンスDTO
 */
public class CalendarResponse {

    /**
     * 日付
     */
    private LocalDate date;

    /**
     * 学習合計時間（分）
     */
    private Integer totalStudyMinutes;
}
```

---

# ■ 4. 統計API

---

## ■ 4.1 日別統計

### ■ クラス名

`DailyStatsResponse`

```java id="2g1y9d"
/**
 * 日別統計レスポンスDTO
 */
public class DailyStatsResponse {

    private LocalDate date;

    private Integer totalStudyMinutes;
}
```

---

## ■ 4.2 月別統計

### ■ クラス名

`MonthlyStatsResponse`

```java id="m8xk4c"
/**
 * 月別統計レスポンスDTO
 */
public class MonthlyStatsResponse {

    /**
     * 年月（yyyy-MM）
     */
    private String month;

    private Integer totalStudyMinutes;
}
```

---

## ■ 4.3 科目別統計

### ■ クラス名

`SubjectStatsResponse`

```java id="7v1kqp"
/**
 * 科目別統計レスポンスDTO
 */
public class SubjectStatsResponse {

    private String subject;

    private Integer totalStudyMinutes;
}
```

---

# ■ 最終まとめ（Phase2〜3 DTO）

---

## ■ 追加DTO一覧

### タグ

* TagCreateRequest
* TagCreateResponse
* TagListResponse
* TagResponse
* TagDeleteResponse

---

### 学習記録（拡張）

* StudyRecordCreateRequest（tags追加）
* StudyRecordUpdateRequest（tags追加）
* StudyRecordSummary（tags追加）
* StudyRecordDetailResponse（tags追加）
* StudyRecordSearchCondition

---

### カレンダー

* CalendarResponse

---

### 統計

* DailyStatsResponse
* MonthlyStatsResponse
* SubjectStatsResponse
