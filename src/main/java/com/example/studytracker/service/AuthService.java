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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証関連のビジネスロジックを担当するService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * ユーザー登録を行う
     * 
     * @param request 登録リクエスト
     * @return 登録レスポンス
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // username重複チェック
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("usernameは既に使用されています");
        }

        // passwordをBCryptでハッシュ化
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // User生成
        User user = User.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .githubUsername(request.getGithubUsername())
                .build();

        // DB保存
        User savedUser = userRepository.save(user);

        // レスポンス返却
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .githubUsername(savedUser.getGithubUsername())
                .build();
    }

    /**
     * ログインを行う
     * usernameでユーザーを取得し、パスワード照合後にJWTを生成して返却する
     * 認証失敗時は詳細な理由を返さない（セキュリティ対策）
     *
     * @param request ログインリクエスト
     * @return ログインレスポンス（JWTトークン）
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // usernameでユーザー取得
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("認証に失敗しました"));

        // パスワード照合（BCrypt）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("認証に失敗しました");
        }

        // JWT生成
        String token = jwtUtil.generateToken(user.getId().toString());

        // レスポンス返却
        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}
