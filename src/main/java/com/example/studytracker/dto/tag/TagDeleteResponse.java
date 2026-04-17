package com.example.studytracker.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * タグ削除レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDeleteResponse {

    /**
     * 削除完了メッセージ
     */
    private String message;
}
