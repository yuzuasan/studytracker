package com.example.studytracker.testutil;

import java.lang.reflect.Field;

/**
 * テスト用ユーティリティクラス
 * リフレクションを使用してプライベートフィールドに値を設定する機能を提供
 */
public class TestUtil {

    /**
     * リフレクションを使用してプライベートフィールドに値を設定する
     * <p>
     * テストコードでDTOのプライベートフィールドに直接値を設定する場合に使用
     * </p>
     *
     * @param target 対象オブジェクト
     * @param fieldName フィールド名
     * @param value 設定する値
     * @throws RuntimeException フィールドの設定に失敗した場合
     */
    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("フィールドの設定に失敗しました: " + fieldName, e);
        }
    }
}
