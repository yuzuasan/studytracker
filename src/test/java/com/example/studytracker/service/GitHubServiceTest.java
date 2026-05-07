package com.example.studytracker.service;

import com.example.studytracker.dto.github.GitHubCommitsRequest;
import com.example.studytracker.dto.github.GitHubCommitsResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.external.github.CommitDto;
import com.example.studytracker.external.github.GitHubApiException;
import com.example.studytracker.external.github.RepositoryDto;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * GitHubServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GitHubService 単体テスト")
class GitHubServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.example.studytracker.external.github.GitHubApiClient gitHubApiClient;

    @InjectMocks
    private GitHubService gitHubService;

    @Nested
    @DisplayName("getCommitCount メソッドのテスト")
    class GetCommitCountTests {

        @Test
        @DisplayName("正常系：githubUsernameが未設定の場合、totalCommits=0を返却する")
        void getCommitCount_Success_GithubUsernameNotSet() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(null)
                    .build();

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // 実行
            GitHubCommitsResponse result = gitHubService.getCommitCount(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getTotalCommits()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, never()).getRepositories(anyString());
        }

        @Test
        @DisplayName("正常系：githubUsernameが空文字列の場合、totalCommits=0を返却する")
        void getCommitCount_Success_GithubUsernameBlank() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername("")
                    .build();

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // 実行
            GitHubCommitsResponse result = gitHubService.getCommitCount(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getTotalCommits()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, never()).getRepositories(anyString());
        }

        @Test
        @DisplayName("正常系：githubUsernameが設定されている場合、コミット数を集計して返却する")
        void getCommitCount_Success_WithCommits() {
            // モックの設定
            Long userId = 1L;
            String githubUsername = "testuser";
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(githubUsername)
                    .build();

            RepositoryDto.OwnerDto ownerDto = RepositoryDto.OwnerDto.builder()
                    .login(githubUsername)
                    .build();

            RepositoryDto repo1 = RepositoryDto.builder()
                    .name("repo1")
                    .owner(ownerDto)
                    .build();

            RepositoryDto repo2 = RepositoryDto.builder()
                    .name("repo2")
                    .owner(ownerDto)
                    .build();

            List<RepositoryDto> repositories = List.of(repo1, repo2);

            List<CommitDto> commits1 = List.of(
                    CommitDto.builder().sha("sha1").build(),
                    CommitDto.builder().sha("sha2").build(),
                    CommitDto.builder().sha("sha3").build()
            );

            List<CommitDto> commits2 = List.of(
                    CommitDto.builder().sha("sha4").build(),
                    CommitDto.builder().sha("sha5").build()
            );

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(gitHubApiClient.getRepositories(githubUsername)).thenReturn(repositories);
            when(gitHubApiClient.getCommits(githubUsername, "repo1", request.getFrom(), request.getTo()))
                    .thenReturn(commits1);
            when(gitHubApiClient.getCommits(githubUsername, "repo2", request.getFrom(), request.getTo()))
                    .thenReturn(commits2);

            // 実行
            GitHubCommitsResponse result = gitHubService.getCommitCount(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getTotalCommits()).isEqualTo(5); // 3 + 2

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, times(1)).getRepositories(githubUsername);
            verify(gitHubApiClient, times(1)).getCommits(githubUsername, "repo1", request.getFrom(), request.getTo());
            verify(gitHubApiClient, times(1)).getCommits(githubUsername, "repo2", request.getFrom(), request.getTo());
        }

        @Test
        @DisplayName("正常系：リポジトリが0件の場合、totalCommits=0を返却する")
        void getCommitCount_Success_NoRepositories() {
            // モックの設定
            Long userId = 1L;
            String githubUsername = "testuser";
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(githubUsername)
                    .build();

            List<RepositoryDto> repositories = List.of();

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(gitHubApiClient.getRepositories(githubUsername)).thenReturn(repositories);

            // 実行
            GitHubCommitsResponse result = gitHubService.getCommitCount(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getTotalCommits()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, times(1)).getRepositories(githubUsername);
            verify(gitHubApiClient, never()).getCommits(anyString(), anyString(), any(), any());
        }

        @Test
        @DisplayName("正常系：コミットがnullの場合、0としてカウントする")
        void getCommitCount_Success_NullCommits() {
            // モックの設定
            Long userId = 1L;
            String githubUsername = "testuser";
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(githubUsername)
                    .build();

            RepositoryDto.OwnerDto ownerDto = RepositoryDto.OwnerDto.builder()
                    .login(githubUsername)
                    .build();

            RepositoryDto repo1 = RepositoryDto.builder()
                    .name("repo1")
                    .owner(ownerDto)
                    .build();

            List<RepositoryDto> repositories = List.of(repo1);

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(gitHubApiClient.getRepositories(githubUsername)).thenReturn(repositories);
            when(gitHubApiClient.getCommits(githubUsername, "repo1", request.getFrom(), request.getTo()))
                    .thenReturn(null);

            // 実行
            GitHubCommitsResponse result = gitHubService.getCommitCount(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getTotalCommits()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, times(1)).getRepositories(githubUsername);
            verify(gitHubApiClient, times(1)).getCommits(githubUsername, "repo1", request.getFrom(), request.getTo());
        }

        @Test
        @DisplayName("異常系：ユーザーが見つからない場合、ResourceNotFoundExceptionがスローされる")
        void getCommitCount_UserNotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            Long userId = 1L;
            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> gitHubService.getCommitCount(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, never()).getRepositories(anyString());
        }

        @Test
        @DisplayName("異常系：リポジトリ一覧取得失敗時、GitHubApiExceptionがスローされる")
        void getCommitCount_GetRepositoriesFails_ThrowsGitHubApiException() {
            // モックの設定
            Long userId = 1L;
            String githubUsername = "testuser";
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(githubUsername)
                    .build();

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(gitHubApiClient.getRepositories(githubUsername))
                    .thenThrow(new GitHubApiException("GitHub APIでリポジトリ一覧の取得に失敗しました"));

            // 実行・検証
            assertThatThrownBy(() -> gitHubService.getCommitCount(request))
                    .isInstanceOf(GitHubApiException.class)
                    .hasMessage("GitHub APIでリポジトリ一覧の取得に失敗しました");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, times(1)).getRepositories(githubUsername);
        }

        @Test
        @DisplayName("異常系：コミット一覧取得失敗時、CompletionExceptionでラップされたGitHubApiExceptionがスローされる")
        void getCommitCount_GetCommitsFails_ThrowsGitHubApiException() {
            // モックの設定
            Long userId = 1L;
            String githubUsername = "testuser";
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .githubUsername(githubUsername)
                    .build();

            RepositoryDto.OwnerDto ownerDto = RepositoryDto.OwnerDto.builder()
                    .login(githubUsername)
                    .build();

            RepositoryDto repo1 = RepositoryDto.builder()
                    .name("repo1")
                    .owner(ownerDto)
                    .build();

            List<RepositoryDto> repositories = List.of(repo1);

            GitHubCommitsRequest request = GitHubCommitsRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(gitHubApiClient.getRepositories(githubUsername)).thenReturn(repositories);
            when(gitHubApiClient.getCommits(githubUsername, "repo1", request.getFrom(), request.getTo()))
                    .thenThrow(new GitHubApiException("GitHub APIでコミット一覧の取得に失敗しました"));

            // 実行・検証
            // 並列処理内で例外が発生するとCompletionExceptionでラップされる
            assertThatThrownBy(() -> gitHubService.getCommitCount(request))
                    .isInstanceOf(java.util.concurrent.CompletionException.class)
                    .cause()
                    .isInstanceOf(GitHubApiException.class)
                    .hasMessage("GitHub APIでコミット一覧の取得に失敗しました");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(userId);
            verify(gitHubApiClient, times(1)).getRepositories(githubUsername);
            verify(gitHubApiClient, times(1)).getCommits(githubUsername, "repo1", request.getFrom(), request.getTo());
        }
    }
}
