package com.example.studytracker.service;

import com.example.studytracker.dto.github.GitHubCommitsRequest;
import com.example.studytracker.dto.github.GitHubCommitsResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.external.github.GitHubApiClient;
import com.example.studytracker.external.github.GitHubApiException;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * GitHub連携関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final GitHubApiClient gitHubApiClient;

    /**
     * 指定期間のGitHubコミット数を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. usersテーブルから githubUsername を取得
     * 3. githubUsernameが未設定の場合は totalCommits = 0 を返却
     * 4. GitHub APIでリポジトリ一覧取得
     * 5. 各リポジトリのコミット数を取得（since/untilでフィルタ）
     * 6. 全リポジトリのコミット数を合計して返却
     *
     * @param request コミット数取得リクエスト
     * @return コミット数取得レスポンス
     * @throws ResourceNotFoundException ユーザーが存在しない場合
     * @throws GitHubApiException GitHub API呼び出し失敗時
     */
    @Transactional(readOnly = true)
    public GitHubCommitsResponse getCommitCount(GitHubCommitsRequest request) {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. usersテーブルから githubUsername を取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        String githubUsername = user.getGithubUsername();

        // 3. githubUsernameが未設定の場合は totalCommits = 0 を返却
        if (githubUsername == null || githubUsername.isBlank()) {
            log.debug("[{}] getCommitCount: githubUsername is not set, returning 0",
                    this.getClass().getSimpleName());
            return GitHubCommitsResponse.builder()
                    .totalCommits(0)
                    .build();
        }

        // 4. GitHub APIでリポジトリ一覧取得
        List<com.example.studytracker.external.github.RepositoryDto> repositories =
                gitHubApiClient.getRepositories(githubUsername);

        // 5. 各リポジトリのコミット数を取得して合計
        int totalCommits = 0;
        LocalDate from = request.getFrom();
        LocalDate to = request.getTo();

        for (com.example.studytracker.external.github.RepositoryDto repo : repositories) {
            String owner = repo.getOwner().getLogin();
            String repoName = repo.getName();

            List<com.example.studytracker.external.github.CommitDto> commits =
                    gitHubApiClient.getCommits(owner, repoName, from, to);

            if (commits != null) {
                totalCommits += commits.size();
            }
        }

        log.debug("[{}] getCommitCount result: totalCommits={}",
                this.getClass().getSimpleName(), totalCommits);

        // 6. レスポンス返却
        return GitHubCommitsResponse.builder()
                .totalCommits(totalCommits)
                .build();
    }
}
