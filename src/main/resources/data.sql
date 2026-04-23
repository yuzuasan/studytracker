-- ============================================
-- Phase1 テストデータ
-- users, study_records テーブル
-- ============================================

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

-- ============================================
-- 月別統計確認用テストデータ（過去の月）
-- ============================================

-- ユーザー1（tanaka_taro）の3月の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, '2026-03-15', 'Javaプログラミング', 180, 'Spring Bootの基礎を学習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, '2026-03-20', 'データベース', 120, 'SQLの基本文法を復習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, '2026-03-25', '英語', 45, '英単語の暗記', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー1（tanaka_taro）の2月の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, '2026-02-10', 'Javaプログラミング', 90, 'Javaの基本構文を学習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (1, '2026-02-28', 'データベース', 60, 'データベース設計の基礎', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー2（yamamoto_hanako）の3月の学習記録
INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (2, '2026-03-05', 'Python', 150, 'NumPyの配列操作を学習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO study_records (user_id, study_date, subject, study_minutes, memo, created_at, updated_at)
VALUES (2, '2026-03-18', '機械学習', 200, '教師あり学習の基礎概念', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- Phase2 テストデータ
-- tags, study_record_tags テーブル
-- ============================================

-- ============================================
-- tags テストデータ
-- ============================================

-- ユーザー1（tanaka_taro）のタグ
INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (1, 'Java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (1, 'データベース', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (1, '英語', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー2（yamamoto_hanako）のタグ
INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (2, 'Python', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (2, '機械学習', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ユーザー3（suzuki_kenji）のタグ
INSERT INTO tags (user_id, name, created_at, updated_at)
VALUES (3, 'JavaScript', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- study_record_tags テストデータ
-- ============================================

-- ユーザー1の学習記録とタグの関連付け
-- 学習記録1: Javaプログラミング → Java + データベース（複数タグ）
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (1, 1);
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (1, 2);

-- 学習記録2: データベース → データベースタグ
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (2, 2);

-- 学習記録3: Javaプログラミング → Javaタグ（タグ単体）
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (3, 1);

-- 学習記録4: 英語 → 英語タグ
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (4, 3);

-- 学習記録5: データベース → タグなし（タグ未紐付けのテスト用）
-- ※この学習記録にはタグを紐付けない

-- ユーザー2の学習記録とタグの関連付け
-- 学習記録6: Python → Python + 機械学習（複数タグ）
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (6, 4);
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (6, 5);

-- 学習記録7: 機械学習 → 機械学習タグ（タグ単体）
INSERT INTO study_record_tags (study_record_id, tag_id)
VALUES (7, 5);

-- ユーザー3の学習記録とタグの関連付け
-- 学習記録8: JavaScript → タグなし（タグ未紐付けのテスト用）
-- ※この学習記録にはタグを紐付けない
