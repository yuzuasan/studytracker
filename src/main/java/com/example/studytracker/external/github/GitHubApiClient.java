package com.example.studytracker.external.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GitHub APIクライアントクラス
 * <p>
 * GitHub APIを呼び出してリポジトリ一覧とコミット一覧を取得する。
 * WebClientを使用してHTTPリクエストを実行する。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    private final WebClient githubWebClient;

    /**
     * ユーザーのリポジトリ一覧を取得する
     * <p>
     * GET /users/{username}/repos を呼び出す。
     * publicリポジトリを対象とする。
     * </p>
     *
     * @param username GitHubユーザー名
     * @return リポジトリ一覧
     * @throws GitHubApiException GitHub API呼び出し失敗時
     */
    public List<RepositoryDto> getRepositories(String username) {
        try {

            return githubWebClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .bodyToFlux(RepositoryDto.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[{}] getRepositories error: status={}, body={}",
                    this.getClass().getSimpleName(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new GitHubApiException("GitHub APIでリポジトリ一覧の取得に失敗しました", e);
        } catch (Exception e) {
            log.error("[{}] getRepositories error: {}", this.getClass().getSimpleName(), e.getMessage());
            throw new GitHubApiException("GitHub APIでリポジトリ一覧の取得に失敗しました", e);
        }
    }

    /**
     * 指定リポジトリのコミット一覧を取得する
     * <p>
     * GET /repos/{owner}/{repo}/commits を呼び出す。
     * since/untilクエリパラメータで期間をフィルタリングする。
     * ページング対応で最大1000件（10ページ）まで取得する。
     * </p>
     *
     * @param owner オーナー名
     * @param repo リポジトリ名
     * @param from 開始日
     * @param to 終了日
     * @return コミット一覧
     * @throws GitHubApiException GitHub API呼び出し失敗時
     */
    public List<CommitDto> getCommits(String owner, String repo, LocalDate from, LocalDate to) {
        // 最大ページ数を10に固定（1000件まで取得）
        final int maxPages = 10;
        return getCommitsWithPaging(owner, repo, from, to, 1, maxPages);
    }

    /**
     * ページングを使用してコミット一覧を取得する（内部メソッド）
     *
     * @param owner オーナー名
     * @param repo リポジトリ名
     * @param from 開始日
     * @param to 終了日
     * @param page ページ番号（1開始）
     * @param maxPages 最大ページ数
     * @return コミット一覧
     * @throws GitHubApiException GitHub API呼び出し失敗時
     */
    private List<CommitDto> getCommitsWithPaging(String owner, String repo, LocalDate from, LocalDate to,
                                                   int page, int maxPages) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

            // ラムダ式で使用するため、初期化時に値を確定させる（実質的に final）
            final String since = from != null
                    ? from.atStartOfDay(zoneId).format(formatter)
                    : null;
            final String until = to != null
                    ? to.atTime(23, 59, 59).atZone(zoneId).format(formatter)
                    : null;

            List<CommitDto> commits = githubWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/commits")
                            .queryParam("since", since)
                            .queryParam("until", until)
                            .queryParam("per_page", 100)
                            .queryParam("page", page)
                            .build(owner, repo))
                    .retrieve()
                    .bodyToFlux(CommitDto.class)
                    .collectList()
                    .block();

            // 結果がnullの場合は空リストを返す
            if (commits == null) {
                return List.of();
            }

            // 結果が100件未満の場合はこれ以上ページがないため終了
            if (commits.size() < 100) {
                return commits;
            }

            // 最大ページ数に達した場合は終了
            if (page >= maxPages) {
                log.debug("[{}] getCommitsWithPaging: reached max pages ({}), repo={}/{}, returning {} commits",
                        this.getClass().getSimpleName(), maxPages, owner, repo, commits.size());
                return commits;
            }

            // 次ページを取得して結合
            List<CommitDto> nextCommits = getCommitsWithPaging(owner, repo, from, to, page + 1, maxPages);
            List<CommitDto> allCommits = new ArrayList<>(commits);
            allCommits.addAll(nextCommits);

            return allCommits;
        } catch (WebClientResponseException e) {
            log.error("[{}] getCommitsWithPaging error: status={}, body={}",
                    this.getClass().getSimpleName(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new GitHubApiException("GitHub APIでコミット一覧の取得に失敗しました", e);
        } catch (Exception e) {
            log.error("[{}] getCommitsWithPaging error: {}", this.getClass().getSimpleName(), e.getMessage());
            throw new GitHubApiException("GitHub APIでコミット一覧の取得に失敗しました", e);
        }
    }
}
