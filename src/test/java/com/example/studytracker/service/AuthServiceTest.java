package com.example.studytracker.service;

import com.example.studytracker.dto.auth.LoginRequest;
import com.example.studytracker.dto.auth.LoginResponse;
import com.example.studytracker.dto.auth.RegisterRequest;
import com.example.studytracker.dto.auth.RegisterResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 単体テスト")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // テスト用データのセットアップ
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setGithubUsername("githubuser");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .githubUsername("githubuser")
                .build();
    }

    @Nested
    @DisplayName("register メソッドのテスト")
    class RegisterTests {

        @Test
        @DisplayName("正常系：新規ユーザー登録が成功する")
        void register_Success() {
            // モックの設定
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            // 実行
            RegisterResponse response = authService.register(registerRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getGithubUsername()).isEqualTo("githubuser");

            // モックの呼び出し検証
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(passwordEncoder, times(1)).encode("password123");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("異常系：usernameが重複している場合、ConflictExceptionがスローされる")
        void register_UsernameAlreadyExists_ThrowsConflictException() {
            // モックの設定：usernameが既に存在
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            // 実行・検証
            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("usernameは既に使用されています");

            // モックの呼び出し検証
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("正常系：githubUsernameがnullでも登録が成功する")
        void register_WithNullGithubUsername_Success() {
            // githubUsernameをnullに設定
            registerRequest.setGithubUsername(null);

            // モックの設定
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
            
            User userWithoutGithub = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("hashedPassword")
                    .githubUsername(null)
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(userWithoutGithub);

            // 実行
            RegisterResponse response = authService.register(registerRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getGithubUsername()).isNull();

            // モックの呼び出し検証
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login メソッドのテスト")
    class LoginTests {

        @Test
        @DisplayName("正常系：正しいusernameとpasswordでログインが成功する")
        void login_Success() {
            // モックの設定
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
            when(jwtUtil.generateToken("1")).thenReturn("jwt.token.here");

            // 実行
            LoginResponse response = authService.login(loginRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
            assertThat(response.getTokenType()).isEqualTo("Bearer");

            // モックの呼び出し検証
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(passwordEncoder, times(1)).matches("password123", "hashedPassword");
            verify(jwtUtil, times(1)).generateToken("1");
        }

        @Test
        @DisplayName("異常系：存在しないusernameの場合、UnauthorizedExceptionがスローされる")
        void login_UserNotFound_ThrowsUnauthorizedException() {
            // モックの設定：ユーザーが存在しない
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証に失敗しました");

            // モックの呼び出し検証
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("異常系：パスワードが不一致の場合、UnauthorizedExceptionがスローされる")
        void login_PasswordMismatch_ThrowsUnauthorizedException() {
            // モックの設定：ユーザーは存在するがパスワードが不一致
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

            // 実行・検証
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証に失敗しました");

            // モックの呼び出し検証
            verify(userRepository, times(1)).findByUsername("testuser");
            verify(passwordEncoder, times(1)).matches("password123", "hashedPassword");
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("セキュリティ対策：認証失敗時のエラーメッセージが詳細を含まない")
        void login_Security_MessageDoesNotRevealDetails() {
            // モックの設定：ユーザーが存在しない
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

            // 実行・検証：エラーメッセージが詳細を含まないことを確認
            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証に失敗しました");
        }
    }
}
