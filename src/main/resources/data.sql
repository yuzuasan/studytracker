-- ============================================
-- Phase1 テストデータ
-- users, study_records テーブル
-- ============================================

-- 既存データの削除（クリーンアップ用、必要に応じて使用）
-- DELETE FROM study_records;
-- DELETE FROM users;

-- ============================================
-- users テストデータ ※パスワードは「password」固定
-- ============================================

-- ユーザー1: 主要テストユーザー（複数の学習記録を持つ）
INSERT INTO users (username, password, github_username, created_at, updated_at)
VALUES ('tanaka_taro', '$2a$10$oKnu1dnKu37igYotc2EySuwRnT1TBkejDtHevb07xRkvwg5AT1tD.', 'tanaka-github', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー2: 別ユーザーのテスト用
INSERT INTO users (username, password, github_username, created_at, updated_at)
VALUES ('yamamoto_hanako', '$2a$10$oKnu1dnKu37igYotc2EySuwRnT1TBkejDtHevb07xRkvwg5AT1tD.', 'yamamoto-dev', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー3: GitHub連携なしのユーザー
INSERT INTO users (username, password, github_username, created_at, updated_at)
VALUES ('suzuki_kenji', '$2a$10$oKnu1dnKu37igYotc2EySuwRnT1TBkejDtHevb07xRkvwg5AT1tD.', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- study_records テストデータ
-- ============================================

-- ユーザー1（tanaka_taro）の学習記録

-- 今日の学習記録（メモあり）
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, CURRENT_DATE, 'Javaプログラミング', 120, 'Spring Bootのコントローラー層を学習。REST APIの設計について復習した。', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 昨日の学習記録（メモなし）
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, CURRENT_DATE - 1, 'データベース', 90, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2日前の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, CURRENT_DATE - 2, 'Javaプログラミング', 60, 'Stream APIの基本操作を練習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3日前の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, CURRENT_DATE - 3, '英語', 30, 'TOEICのリスニング対策', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 1週間前の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, CURRENT_DATE - 7, 'データベース', 150, 'MySQLのインデックス設計について学習。EXPLAINの使い方を理解した。', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー2（yamamoto_hanako）の学習記録

-- 今日の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (2, CURRENT_DATE, 'Python', 45, 'pandasの基本操作', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 昨日の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (2, CURRENT_DATE - 1, '機械学習', 120, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー3（suzuki_kenji）の学習記録

-- 最小限のデータのみ
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (3, CURRENT_DATE - 1, 'JavaScript', 60, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

