# ■ DTO設計（Phase4～5）

---

# ■ 1. 目標管理API

---

## ■ POST /goals

### ■ Request DTO

```java
public class GoalCreateRequest {
    private String month;
    private Integer targetMinutes;
}
```

---

### ■ Response DTO

```java
public class GoalCreateResponse {
    private Long id;
}
```

---

---

## ■ GET /goals

### ■ Response DTO（一覧）

```java
public class GoalListResponse {
    private List<GoalSummary> goals;

    public static class GoalSummary {
        private Long id;
        private String month;
        private Integer targetMinutes;
        private Integer achievedMinutes;
    }
}
```

---

---

## ■ PUT /goals/{id}

### ■ Request DTO

```java
public class GoalUpdateRequest {
    private Integer targetMinutes;
}
```

---

### ■ Response DTO

```java
public class GoalUpdateResponse {
    private Long id;
}
```

---

---

## ■ DELETE /goals/{id}

### ■ Response DTO

```java
public class GoalDeleteResponse {
    private String message;
}
```

---

# ■ 2. GitHub連携API

---

## ■ GET /github/commits

### ■ Request DTO（クエリパラメータ）

```java
public class GitHubCommitsRequest {
    private LocalDate from;
    private LocalDate to;
}
```

---

### ■ Response DTO

```java
public class GitHubCommitsResponse {
    private Integer totalCommits;
}
```

---

# ■ 3. ストリークAPI

---

## ■ GET /streak

### ■ Response DTO

```java
public class StreakResponse {
    private Integer currentStreak;
}
```

---

# ■ 4. エクスポートAPI

---

## ■ GET /export/csv

### ■ Request DTO（クエリパラメータ）

```java
public class ExportCsvRequest {
    private LocalDate from;
    private LocalDate to;
}
```

---

### ■ Response DTO

※ CSV出力のためDTOは定義しない

---

# ■ DTO一覧まとめ

---

## ■ Request DTO

| API                 | DTO                  |
| ------------------- | -------------------- |
| POST /goals         | GoalCreateRequest    |
| PUT /goals/{id}     | GoalUpdateRequest    |
| GET /github/commits | GitHubCommitsRequest |
| GET /export/csv     | ExportCsvRequest     |

---

## ■ Response DTO

| API                 | DTO                   |
| ------------------- | --------------------- |
| POST /goals         | GoalCreateResponse    |
| GET /goals          | GoalListResponse      |
| PUT /goals/{id}     | GoalUpdateResponse    |
| DELETE /goals/{id}  | GoalDeleteResponse    |
| GET /github/commits | GitHubCommitsResponse |
| GET /streak         | StreakResponse        |
