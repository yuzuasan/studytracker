package com.example.studytracker.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * タグ一覧取得レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagListResponse {

    /**
     * タグのリスト
     */
    private List<TagResponse> tags;

    /**
     * タグ情報
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagResponse {

        /**
         * タグID
         */
        private Long id;

        /**
         * タグ名
         */
        private String name;
    }
}
