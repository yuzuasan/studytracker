# ■ DTO設計（Phase1）

---

# ■ 1. 認証API

---

## ■ POST /auth/register

### ■ Request DTO

```java
public class RegisterRequest {
    private String username;
    private String password;
    private String githubUsername;
}
```

---

### ■ Response DTO

```java
public class RegisterResponse {
    private Long userId;
    private String username;
    private String githubUsername;
}
```

---

---

## ■ POST /auth/login

### ■ Request DTO

```java
public class LoginRequest {
    private String username;
    private String password;
}
```

---

### ■ Response DTO

```java
public class LoginResponse {
    private String accessToken;
    private String tokenType;
}
```

---

# ■ 2. ユーザーAPI

---

## ■ GET /users/me

### ■ Response DTO

```java
public class UserResponse {
    private Long userId;
    private String username;
    private String githubUsername;
}
```

---

# ■ 3. 学習記録API

---

## ■ POST /study-records

### ■ Request DTO

```java
public class StudyRecordCreateRequest {
    private LocalDate date;
    private String subject;
    private Integer studyMinutes;
    private String memo;
}
```

---

### ■ Response DTO

```java
public class StudyRecordCreateResponse {
    private Long id;
}
```

---

---

## ■ GET /study-records

### ■ Response DTO（一覧）

```java
public class StudyRecordListResponse {
    private List<StudyRecordSummary> records;

    public static class StudyRecordSummary {
        private Long id;
        private LocalDate date;
        private String subject;
        private Integer studyMinutes;
    }
}
```

---

---

## ■ GET /study-records/{id}

### ■ Response DTO

```java
public class StudyRecordDetailResponse {
    private Long id;
    private LocalDate date;
    private String subject;
    private Integer studyMinutes;
    private String memo;
}
```

---

---

## ■ PATCH /study-records/{id}

### ■ Request DTO（部分更新）

```java
public class StudyRecordUpdateRequest {
    private LocalDate date;
    private String subject;
    private Integer studyMinutes;
    private String memo;
}
```

---

### ■ Response DTO

```java
public class StudyRecordUpdateResponse {
    private Long id;
}
```

---

---

## ■ DELETE /study-records/{id}

### ■ Response DTO

```java
public class StudyRecordDeleteResponse {
    private String message;
}
```

---

# ■ DTO一覧まとめ

---

## ■ Request DTO

| API                       | DTO                      |
| ------------------------- | ------------------------ |
| POST /auth/register       | RegisterRequest          |
| POST /auth/login          | LoginRequest             |
| POST /study-records       | StudyRecordCreateRequest |
| PATCH /study-records/{id} | StudyRecordUpdateRequest |

---

## ■ Response DTO

| API                        | DTO                       |
| -------------------------- | ------------------------- |
| POST /auth/register        | RegisterResponse          |
| POST /auth/login           | LoginResponse             |
| GET /users/me              | UserResponse              |
| POST /study-records        | StudyRecordCreateResponse |
| GET /study-records         | StudyRecordListResponse   |
| GET /study-records/{id}    | StudyRecordDetailResponse |
| PATCH /study-records/{id}  | StudyRecordUpdateResponse |
| DELETE /study-records/{id} | StudyRecordDeleteResponse |
