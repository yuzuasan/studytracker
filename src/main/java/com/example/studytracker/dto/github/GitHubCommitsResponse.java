package com.example.studytracker.dto.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GitHubコミット数取得レスポンスDTO
 * <p>
 * GET /github/commits のレスポンスとして使用する。
 * 指定期間のコミット総数を返却する。
 * </p>
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubCommitsResponse {

    /**
     * コミット総数
     * <p>
     * 指定期間内の全リポジトリのコミット数の合計。
     * githubUsernameが未設定の場合は0を返却する。
     * </p>
     */
    private Integer totalCommits;
}
