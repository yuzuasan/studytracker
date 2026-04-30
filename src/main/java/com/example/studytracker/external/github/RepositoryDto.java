package com.example.studytracker.external.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * GitHubリポジトリ情報DTO
 * <p>
 * GitHub APIから取得したリポジトリ情報を格納する。
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryDto {

    /**
     * リポジトリ名
     */
    private String name;

    /**
     * オーナー名
     */
    @JsonProperty("owner")
    private OwnerDto owner;

    /**
     * オーナー情報DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OwnerDto {
        /**
         * オーナー名（ユーザー名）
         */
        private String login;
    }
}
