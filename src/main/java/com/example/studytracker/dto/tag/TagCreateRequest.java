package com.example.studytracker.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * タグ作成リクエストDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagCreateRequest {

    /**
     * タグ名
     * 必須 / 1〜50文字
     */
    @NotBlank(message = "nameは必須です")
    @Size(max = 50, message = "nameは50文字で入力してください")
    private String name;
}
