# ■ StudyTracker API 詳細設計

## 1. リクエスト共通仕様

### ■ Content-Type

```
application/json
```

### ■ 認証ヘッダ（ログイン後）

```
Authorization: Bearer {JWT}
```

---

## 2. バリデーション共通ルール

| 項目    | ルール          |
| ----- | ------------ |
| 文字列   | trim後で判定     |
| 日付    | YYYY-MM-DD   |
| 数値    | 0以上          |
| 時間（分） | 1〜1440       |
| ID    | Long（正の整数）   |
| 必須項目  | null / 空文字禁止 |

---

## 3. レスポンス共通仕様

### ■ 正常レスポンス

```json
{
  "status": "success",
  "data": {}
}
```

### ■ エラーレスポンス

```json
{
  "status": "error",
  "message": "エラーメッセージ"
}
```

### ■ バリデーションエラー（拡張）

```json
{
  "status": "error",
  "message": "Validation error",
  "errors": [
    {
      "field": "email",
      "message": "must be valid email"
    }
  ]
}
```

---

## 4. HTTPステータス共通設計

| ステータス                     | 使用ケース       |
| ------------------------- | ----------- |
| 200 OK                    | 取得・更新成功     |
| 201 Created               | 新規作成成功      |
| 400 Bad Request           | バリデーションエラー  |
| 401 Unauthorized          | 認証エラー       |
| 404 Not Found             | データなし       |
| 409 Conflict              | データ競合・状態不整合 |
| 500 Internal Server Error | サーバエラー      |

---

# ■ ② 認証API

## 2.1 ユーザー登録

### POST /auth/register

### ■ リクエスト

```json
{
  "username": "string",
  "password": "string",
  "githubUsername": "string"
}
```

---

### ■ バリデーション

| 項目             | 条件           |
| -------------- | ------------ |
| username       | 必須 / 1〜50文字  |
| password       | 必須 / 8〜100文字 |
| githubUsername | 任意 / 1〜100文字 |

---

### ■ レスポンス（201）

```json
{
  "status": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "githubUsername": "test-github"
  }
}
```

---

### ■ エラー

| ステータス | 内容         |
| ----- | ---------- |
| 400   | 入力不正       |
| 409   | username重複 |

---

## 2.2 ログイン

### POST /auth/login

### ■ リクエスト

```json
{
  "username": "string",
  "password": "string"
}
```

---

### ■ バリデーション

| 項目       | 条件 |
| -------- | -- |
| username | 必須 |
| password | 必須 |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer"
  }
}
```

---

### ■ エラー

| ステータス | 内容   |
| ----- | ---- |
| 400   | 入力不正 |
| 401   | 認証失敗 |

---

# ■ ③ ユーザーAPI

## 3.1 自分の情報取得

### GET /users/me

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "githubUsername": "test-github"
  }
}
```

---

### ■ エラー

| ステータス | 内容  |
| ----- | --- |
| 401   | 未認証 |

---

# ■ ④ 学習記録API（コア）

## 4.1 学習記録登録

### POST /study-records

### ■ リクエスト

```json
{
  "date": "2026-03-31",
  "subject": "Java",
  "studyMinutes": 120,
  "memo": "Spring Boot学習",
  "tags": ["backend", "spring"]
}
```

---

### ■ バリデーション

| 項目           | 条件              |
| ------------ | --------------- |
| date         | 必須 / YYYY-MM-DD |
| subject      | 必須 / 1〜100文字    |
| studyMinutes | 必須 / 1〜1440     |
| memo         | 任意 / 0〜1000文字   |
| tags         | 任意 / 最大10件      |
| tagsの各要素     | 1〜50文字          |

---

### ■ レスポンス（201）

```json
{
  "status": "success",
  "data": {
    "id": 1
  }
}
```

---

### ■ エラー

| ステータス | 内容   |
| ----- | ---- |
| 400   | 入力不正 |
| 401   | 未認証  |

---

## 4.2 学習記録一覧取得

### GET /study-records

### ■ クエリパラメータ

| パラメータ   | 型      | フォーマット     | 内容   |
| ------- | ------ | ---------- | ---- |
| from    | String | YYYY-MM-DD | 開始日  |
| to      | String | YYYY-MM-DD | 終了日  |
| tag     | String | タグ名        | タグ検索 |
| keyword | String | 任意文字列      | メモ検索 |

---

### ■ リクエスト例

```
GET /study-records?from=2026-03-01&to=2026-03-31
GET /study-records?tag=spring
GET /study-records?keyword=Boot
```

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "date": "2026-03-31",
      "subject": "Java",
      "studyMinutes": 120,
      "tags": ["spring"]
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容                |
| ----- | ----------------- |
| 400   | クエリ不正（フォーマット違反など） |
| 401   | 未認証               |

---

## 4.3 学習記録詳細

### GET /study-records/{id}

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "date": "2026-03-31",
    "subject": "Java",
    "studyMinutes": 120,
    "memo": "Spring Boot",
    "tags": ["spring"]
  }
}
```

---

### ■ エラー

| ステータス | 内容    |
| ----- | ----- |
| 401   | 未認証   |
| 404   | データなし |

## 4.4 学習記録更新

### PATCH /study-records/{id}

### ■ リクエスト

※更新したい項目のみ指定（部分更新）

```json
{
  "date": "2026-03-31",
  "studyMinutes": 90
}
```

---

### ■ バリデーション

| 項目           | 条件                  |
| ------------ | ------------------- |
| id           | 必須 / パスパラメータ / 正の整数 |
| date         | 任意 / YYYY-MM-DD     |
| subject      | 任意 / 1〜100文字        |
| studyMinutes | 任意 / 1〜1440         |
| memo         | 任意 / 0〜1000文字       |
| tags         | 任意 / 最大10件          |
| tagsの各要素     | 1〜50文字              |
| 全項目          | 少なくとも1項目は必須         |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "id": 1
  }
}
```

---

### ■ エラー

| ステータス | 内容             |
| ----- | -------------- |
| 400   | 入力不正（空リクエスト含む） |
| 401   | 未認証            |
| 404   | データなし          |

---

## 4.5 学習記録削除

### DELETE /study-records/{id}

---

### ■ バリデーション

| 項目 | 条件        |
| -- | --------- |
| id | 必須 / 正の整数 |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "message": "deleted"
  }
}
```

---

### ■ エラー

| ステータス | 内容    |
| ----- | ----- |
| 401   | 未認証   |
| 404   | データなし |

---

# ■ ⑤ タグ管理API

## 5.1 タグ作成

### POST /tags

### ■ リクエスト

```json
{
  "name": "spring"
}
```

---

### ■ バリデーション

| 項目   | 条件          |
| ---- | ----------- |
| name | 必須 / 1〜50文字 |

---

### ■ レスポンス（201）

```json
{
  "status": "success",
  "data": {
    "id": 1
  }
}
```

---

### ■ エラー

| ステータス | 内容   |
| ----- | ---- |
| 400   | 入力不正 |
| 401   | 未認証  |
| 409   | 重複   |

---

## 5.2 タグ一覧取得

### GET /tags

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "spring"
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容  |
| ----- | --- |
| 401   | 未認証 |

---

## 5.3 タグ削除

### DELETE /tags/{id}

---

### ■ バリデーション

| 項目 | 条件        |
| -- | --------- |
| id | 必須 / 正の整数 |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "message": "deleted"
  }
}
```

---

### ■ エラー

| ステータス | 内容    |
| ----- | ----- |
| 401   | 未認証   |
| 404   | データなし |

---

# ■ ⑥ カレンダーAPI

## 6.1 学習カレンダー取得

### GET /calendar

---

### ■ クエリパラメータ

| パラメータ | 型       | フォーマット | 内容 |
| ----- | ------- | ------ | -- |
| year  | Integer | YYYY   | 年  |
| month | Integer | 1〜12   | 月  |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "date": "2026-03-01",
      "totalStudyMinutes": 120
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容      |
| ----- | ------- |
| 400   | パラメータ不正 |
| 401   | 未認証     |

---

# ■ ⑦ 学習統計API

## 7.1 日別統計

### GET /stats/daily

---

### ■ クエリパラメータ

| パラメータ | 型      | 条件              |
| ----- | ------ | --------------- |
| from  | String | 必須 / YYYY-MM-DD |
| to    | String | 必須 / YYYY-MM-DD |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "date": "2026-03-31",
      "totalStudyMinutes": 120
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容      |
| ----- | ------- |
| 400   | パラメータ不正 |
| 401   | 未認証     |

---

## 7.2 月別統計

### GET /stats/monthly

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "month": "2026-03",
      "totalStudyMinutes": 3200
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容  |
| ----- | --- |
| 401   | 未認証     |

---

## 7.3 科目別統計

### GET /stats/subjects

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "subject": "Java",
      "totalStudyMinutes": 1500
    }
  ]
}
```

---

### ■ エラー（共通）

| ステータス | 内容      |
| ----- | ------- |
| 401   | 未認証     |

---

# ■ ⑧ 目標管理API

## 8.1 目標設定

### POST /goals

### ■ リクエスト

```json
{
  "month": "2026-04",
  "targetMinutes": 3000
}
```

---

### ■ バリデーション

| 項目            | 条件           |
| ------------- | ------------ |
| month         | 必須 / YYYY-MM |
| targetMinutes | 必須 / 1〜20000 |

---

### ■ レスポンス（201）

```json
{
  "status": "success",
  "data": {
    "id": 1
  }
}
```

---

### ■ エラー

| ステータス | 内容       |
| ----- | -------- |
| 400   | 入力不正     |
| 401   | 未認証      |
| 409   | 重複（月が重複） |

---

## 8.2 目標取得

### GET /goals

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "month": "2026-04",
      "targetMinutes": 3000,
      "achievedMinutes": 1200
    }
  ]
}
```

---

### ■ エラー

| ステータス | 内容  |
| ----- | --- |
| 401   | 未認証 |

---

## 8.3 目標更新

### PUT /goals/{id}

---

### ■ リクエスト

```json
{
  "targetMinutes": 3500
}
```

---

### ■ バリデーション

| 項目            | 条件           |
| ------------- | ------------ |
| targetMinutes | 必須 / 1〜20000 |

---
### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "id": 1
  }
}
```

---

### ■ エラー

| ステータス | 内容    |
| ----- | ----- |
| 400   | 入力不正  |
| 401   | 未認証   |
| 404   | データなし |

---

## 8.4 目標削除

### DELETE /goals/{id}

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "message": "deleted"
  }
}
```

---

### ■ エラー

| ステータス | 内容    |
| ----- | ----- |
| 401   | 未認証   |
| 404   | データなし |

---

# ■ ⑨ GitHub連携API

## 9.1 コミット数取得

### GET /github/commits

---

### ■ クエリパラメータ

| パラメータ | 型      | フォーマット     |
| ----- | ------ | ---------- |
| from  | String | YYYY-MM-DD |
| to    | String | YYYY-MM-DD |

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "totalCommits": 25
  }
}
```

---

### ■ エラー

| ステータス | 内容            |
| ----- | ------------- |
| 400   | パラメータ不正       |
| 401   | 未認証           |
| 500   | GitHub APIエラー |

---

# ■ ⑩ ストリークAPI

## 10.1 連続学習日数取得

### GET /streak

---

### ■ レスポンス（200）

```json
{
  "status": "success",
  "data": {
    "currentStreak": 5
  }
}
```

---

### ■ エラー

| ステータス | 内容  |
| ----- | --- |
| 401   | 未認証 |

---

# ■ ⑪ エクスポートAPI

## 11.1 CSVエクスポート

### GET /export/csv

---

### ■ クエリパラメータ

| パラメータ | 型      | 条件              |
| ----- | ------ | --------------- |
| from  | String | 必須 / YYYY-MM-DD |
| to    | String | 必須 / YYYY-MM-DD |

---

### ■ レスポンス（200）

※ Content-Type: text/csv

```
date,subject,minutes,memo
2026-03-31,Java,120,Spring Boot
```

---

### ■ エラー

| ステータス | 内容      |
| ----- | ------- |
| 400   | パラメータ不正 |
| 401   | 未認証     |
