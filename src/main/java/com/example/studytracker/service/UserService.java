package com.example.studytracker.service;

import com.example.studytracker.dto.user.UserResponse;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザー関連のビジネスロジックを担当するServiceクラス
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;

    /**
     * ログインユーザーの情報を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. userIdでユーザー取得
     * 3. DTO変換
     * 4. レスポンス返却
     *
     * @return ユーザーレスポンスDTO
     * @throws ResourceNotFoundException ユーザーが存在しない場合
     */
    public UserResponse getCurrentUser() {
        // 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // userIdでユーザーを取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // DTOに変換して返却
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .githubUsername(user.getGithubUsername())
                .build();
    }
}
