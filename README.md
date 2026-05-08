# StudyTracker

学習記録を管理・分析するためのバックエンドAPIアプリケーションです。

## 学習目的

このプロジェクトは、アプリ開発を通じて以下の技術を習得することを目的としています。

- REST API開発の理解
- JWT認証の実装経験
- CRUD処理の実装
- Spring Bootの実践的な使用

## 概要

### アプリ名
StudyTracker

### 何をするアプリか
学習記録を簡単に登録・管理し、継続的な学習を支援するアプリケーションです。学習時間の集計、カレンダー表示、目標管理などの機能を提供します。

### 対象ユーザー
自己学習者

## 開発ステータス

**Phase 1〜5まで実装完了**

- Phase 1: MVP（認証・学習記録CRUD）
- Phase 2: 検索・タグ機能
- Phase 3: 可視化・分析（カレンダー・統計）
- Phase 4: 目標管理
- Phase 5: 拡張機能（GitHub連携・ストリーク・CSVエクスポート）

## 主な機能

- **認証機能**: ユーザー登録・ログイン（JWT認証）
- **学習記録管理**: 日付、科目、時間、メモ、タグの登録・編集・削除・一覧表示
- **タグ管理**: タグの作成・削除・一覧表示
- **メモ検索**: 学習メモのキーワード検索
- **学習カレンダー**: 月単位の学習状況の可視化
- **学習統計**: 日別・月別・科目別の学習時間集計
- **目標管理**: 月単位の学習時間目標の設定・達成状況確認
- **GitHub連携**: GitHubのコミット数取得
- **ストリーク表示**: 連続学習日数の表示
- **CSVエクスポート**: 学習記録のCSV出力

## 動作環境・要件

- Java 21

## セットアップ手順

### 1. リポジトリのクローン

```bash
git clone https://github.com/your-username/studytracker.git
cd studytracker
```

### 2. 環境変数の設定（GitHub連携を使用する場合）

GitHub連携機能を使用するには、GitHub Personal Access Tokenを設定してください。

**Windows (PowerShell)**
```powershell
$env:GITHUB_TOKEN="your_github_token_here"
```

**Windows (CMD)**
```cmd
set GITHUB_TOKEN=your_github_token_here
```

**Linux/Mac**
```bash
export GITHUB_TOKEN=your_github_token_here
```

※ GitHub連携を使用しない場合は、環境変数の設定は不要です。

### 3. ビルド

```bash
./mvnw clean install
```

または、Mavenがインストールされている場合：

```bash
mvn clean install
```

### 4. 起動

```bash
./mvnw spring-boot:run
```

または、Mavenがインストールされている場合：

```bash
mvn spring-boot:run
```

アプリケーションが起動すると、`http://localhost:8080` でアクセス可能になります。

## APIの使い方

### Swagger UIへのアクセス

アプリケーション起動後、以下のURLからSwagger UIにアクセスできます。

```
http://localhost:8080/swagger-ui.html
```

Swagger UIでは、すべてのAPIエンドポイントの仕様確認と実際のリクエスト送信が可能です。

### 簡単なAPI仕様

#### 認証API

| メソッド | パス             | 概要     |
| -------- | -------------- | ------ |
| POST     | /auth/register | ユーザー登録 |
| POST     | /auth/login    | ログイン   |

#### 学習記録API

| メソッド | パス                  | 概要       |
| -------- | ------------------- | -------- |
| POST     | /study-records      | 学習記録登録   |
| GET      | /study-records      | 学習記録一覧取得 |
| GET      | /study-records/{id} | 学習記録詳細   |
| PATCH    | /study-records/{id} | 学習記録更新   |
| DELETE   | /study-records/{id} | 学習記録削除   |

詳細なAPI仕様はSwagger UIまたは `doc/DD/APIリクエスト・レスポンス.md` を参照してください。

### 認証方式（JWT）

本アプリではJWT（JSON Web Token）による認証を採用しています。

1. **ユーザー登録**: `/auth/register` でユーザーを登録
2. **ログイン**: `/auth/login` でログインし、JWTトークンを取得
3. **API呼び出し**: 取得したトークンをリクエストヘッダーに含めてAPIを呼び出し

```
Authorization: Bearer {your_jwt_token}
```

トークンの有効期限は1時間です。

## 使用技術

### バックエンド
- Java 21
- Spring Boot 4.0.5

### フレームワーク / ライブラリ
- Spring Web（REST API構築）
- Spring Data JPA（DBアクセス）
- Spring Security（認証・認可）
- JWT（JSON Web Token）
- Bean Validation（入力チェック）
- BCrypt（パスワードハッシュ化）
- Swagger（springdoc-openapi、APIドキュメント）
- Lombok（ボイラープレート削減）

### データベース
- H2 Database（開発環境）

### 外部連携
- GitHub REST API

## プロジェクト構成

### パッケージ構成

```
com.example.studytracker
├── controller    # APIエンドポイント定義
├── service       # ビジネスロジック実装
├── repository    # DBアクセス（JPA）
├── entity        # DBテーブル対応
├── dto           # リクエスト/レスポンスDTO
├── security      # JWT認証・セキュリティ制御
├── config        # 設定クラス
├── exception     # 例外管理
├── external      # 外部API連携
├── util          # 汎用処理
└── aspect        # AOP（ログなど）
```

### レイヤー構成

- **Controller**: リクエストを受け付け、DTOで受け渡し
- **Service**: ビジネスロジックを実装、Entityを使用
- **Repository**: DBアクセス、Entityを使用

依存方向を守り、各レイヤーの責務を明確にしています。

## ドキュメント

詳細な設計書は `doc` フォルダに格納されています。

- `要件定義.md`: アプリの要件定義
- `基本設計.md`: システム全体像と機能構成
- `実装フェーズ分割.md`: 開発フェーズと工数見積
- `DD/`: 詳細設計書（API仕様、DTO設計、Entity設計、データベース設計など）

## 制約事項

- **開発環境のみ**: 本アプリは開発環境（H2 Database）での使用を想定しています
- **本番環境未構築**: 本番環境へのデプロイは未実装です
- **フロントエンド未実装**: バックエンドAPIのみの提供です。フロントエンドは含まれていません
- **個人開発用途**: 個人での学習記録管理を目的としています

## ライセンス

MIT License
