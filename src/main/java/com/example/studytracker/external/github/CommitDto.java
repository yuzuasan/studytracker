package com.example.studytracker.external.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * GitHubコミット情報DTO
 * <p>
 * GitHub APIから取得したコミット情報を格納する。
 * コミット数のカウントに使用する。
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitDto {

    /**
     * SHA（コミットハッシュ）
     */
    private String sha;

    /**
     * コミット詳細情報
     */
    @JsonProperty("commit")
    private CommitDetailDto commitDetail;

    /**
     * コミット詳細情報DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommitDetailDto {
        /**
         * コミット日時
         */
        @JsonProperty("committer")
        private CommitterDto committer;
    }

    /**
     * コミッター情報DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommitterDto {
        /**
         * コミット日時（ISO 8601形式）
         */
        private String date;
    }
}
