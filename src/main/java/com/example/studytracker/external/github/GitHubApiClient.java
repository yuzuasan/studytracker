package com.example.studytracker.external.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
     * 最大100件まで取得する。
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

            return githubWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/commits")
                            .queryParam("since", since)
                            .queryParam("until", until)
                            .queryParam("per_page", 100)
                            .build(owner, repo))
                    .retrieve()
                    .bodyToFlux(CommitDto.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[{}] getCommits error: status={}, body={}",
                    this.getClass().getSimpleName(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new GitHubApiException("GitHub APIでコミット一覧の取得に失敗しました", e);
        } catch (Exception e) {
            log.error("[{}] getCommits error: {}", this.getClass().getSimpleName(), e.getMessage());
            throw new GitHubApiException("GitHub APIでコミット一覧の取得に失敗しました", e);
        }
    }
}
