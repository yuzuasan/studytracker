package com.example.studytracker.service;

import com.example.studytracker.dto.auth.RegisterRequest;
import com.example.studytracker.dto.auth.RegisterResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 認証関連のビジネスロジックを担当するService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
