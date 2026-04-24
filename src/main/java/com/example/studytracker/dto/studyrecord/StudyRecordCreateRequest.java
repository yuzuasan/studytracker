package com.example.studytracker.dto.studyrecord;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 学習記録登録リクエストDTO
 */
@Getter
public class StudyRecordCreateRequest {

    /**
     * 学習日
     * 必須 / YYYY-MM-DD形式
     */
    @NotNull(message = "dateは必須です")
    private LocalDate date;

    /**
     * 科目名
     * 必須 / 1〜100文字
     */
    @NotBlank(message = "subjectは必須です")
    @Size(max = 100, message = "subjectは100文字以内で入力してください")
    private String subject;

    /**
     * 学習時間（分）
     * 必須 / 1〜1440分（24時間）
     */
    @NotNull(message = "studyMinutesは必須です")
    @Min(value = 1, message = "studyMinutesは1分以上で入力してください")
    @Max(value = 1440, message = "studyMinutesは1440分以内で入力してください")
    private Integer studyMinutes;

    /**
     * メモ
     * 任意 / 最大1000文字
     */
    @Size(max = 1000, message = "memoは1000文字以内で入力してください")
    private String memo;

    /**
     * タグ名一覧
     * 任意 / 最大10件 / 各タグ1～50文字
     */
    @Size(max = 10, message = "タグは10件以内で入力してください")
    private List<@NotBlank(message = "タグ名は空にできません")
                 @Size(max = 50, message = "タグ名は50文字以内で入力してください") String> tags;

    /**
     * 科目名を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param subject 科目名
     */
    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    /**
     * メモを設定する
     * バリデーション前に前後空白を除去する
     *
     * @param memo メモ
     */
    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    /**
     * タグ名一覧を設定する
     * バリデーション前に各タグ名の前後空白を除去する
     *
     * @param tags タグ名一覧
     */
    public void setTags(List<String> tags) {
        if (tags == null) {
            this.tags = null;
            return;
        }
        this.tags = tags.stream()
                .map(tag -> tag == null ? null : tag.trim())
                .toList();
    }
}
