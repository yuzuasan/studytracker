package com.example.studytracker.external.github;

/**
 * GitHub API例外クラス
 * <p>
 * GitHub API呼び出し失敗時にスローされる例外。
 * </p>
 */
public class GitHubApiException extends RuntimeException {

    public GitHubApiException(String message) {
        super(message);
    }

    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
