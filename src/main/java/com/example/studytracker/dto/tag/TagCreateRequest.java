package com.example.studytracker.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * タグ作成リクエストDTO
 */
@Getter
public class TagCreateRequest {

    /**
     * タグ名
     * 必須 / 1〜50文字
     */
    @NotBlank(message = "nameは必須です")
    @Size(max = 50, message = "nameは50文字で入力してください")
    private String name;

    /**
     * タグ名を設定する
     * バリデーション前に前後空白を除去する
     *
     * @param name タグ名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
