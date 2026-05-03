package com.example.studytracker.service;

import com.example.studytracker.dto.user.UserResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 単体テスト")
class UserServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // テスト用データのセットアップ
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .githubUsername("githubuser")
                .build();
    }

    @Nested
    @DisplayName("getCurrentUser メソッドのテスト")
    class GetCurrentUserTests {

        @Test
        @DisplayName("正常系：ログインユーザーの情報が正しく取得できる")
        void getCurrentUser_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

            // 実行
            UserResponse response = userService.getCurrentUser();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getGithubUsername()).isEqualTo("githubuser");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("正常系：githubUsernameがnullの場合でも正しく取得できる")
        void getCurrentUser_WithNullGithubUsername_Success() {
            // githubUsernameがnullのユーザーを作成
            User userWithoutGithub = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("hashedPassword")
                    .githubUsername(null)
                    .build();

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutGithub));

            // 実行
            UserResponse response = userService.getCurrentUser();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getGithubUsername()).isNull();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("異常系：ユーザーが存在しない場合、ResourceNotFoundExceptionがスローされる")
        void getCurrentUser_UserNotFound_ThrowsResourceNotFoundException() {
            // モックの設定：ユーザーが存在しない
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getCurrentUser_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定：認証情報取得時に例外がスローされる
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, never()).findById(any());
        }
    }
}
