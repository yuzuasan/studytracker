package com.example.studytracker.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * タグ作成レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagCreateResponse {

    /**
     * 作成されたタグのID
     */
    private Long id;
}
