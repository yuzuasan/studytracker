package com.example.studytracker.util;

import com.example.studytracker.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/**
 * 日付関連のバリデーションを担当するユーティリティクラス
 */
@Component
public class DateValidator {

    /**
     * 学習日として許容する最小年（2000年）
     */
    private static final int MIN_YEAR = 2000;

    /**
     * 月として許容する最小値（1月）
     */
    private static final int MIN_MONTH = 1;

    /**
     * 月として許容する最大値（12月）
     */
    private static final int MAX_MONTH = 12;

    /**
     * 現在許容する最大年を取得する
     * <p>
     * 現在年+1年を返却する（カレンダー表示のため来年も許容）
     * </p>
     *
     * @return 最大年（現在年+1）
     */
    private int getMaxYear() {
        return YearMonth.now().getYear() + 1;
    }

    /**
     * 年が有効範囲内か検証する
     * <p>
     * 有効範囲：2000年〜現在年+1年
     * </p>
     *
     * @param year 検証対象の年
     * @throws BadRequestException 年が範囲外の場合
     */
    public void validateYear(int year) {
        int maxYear = getMaxYear();

        if (year < MIN_YEAR || year > maxYear) {
            throw new BadRequestException(
                    String.format("yearは%d年〜%d年の範囲で指定してください", MIN_YEAR, maxYear));
        }
    }

    /**
     * 月が有効範囲内か検証する
     * <p>
     * 有効範囲：1月〜12月
     * </p>
     *
     * @param month 検証対象の月
     * @throws BadRequestException 月が範囲外の場合
     */
    public void validateMonth(int month) {
        if (month < MIN_MONTH || month > MAX_MONTH) {
            throw new BadRequestException("monthは1〜12の範囲で指定してください");
        }
    }

    /**
     * 年と月のペアが有効範囲内か検証する
     *
     * @param year  検証対象の年
     * @param month 検証対象の月
     * @throws BadRequestException 年または月が範囲外の場合
     */
    public void validateYearMonth(int year, int month) {
        validateYear(year);
        validateMonth(month);
    }
}
