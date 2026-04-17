package com.example.studytracker.service;

import com.example.studytracker.dto.tag.TagCreateRequest;
import com.example.studytracker.dto.tag.TagCreateResponse;
import com.example.studytracker.entity.Tag;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.TagRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * タグ関連のビジネスロジックを担当するServiceクラス
 */
@Service
@RequiredArgsConstructor
public class TagService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    /**
     * タグを作成する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. タグ名の前後空白を除去（trim）
     * 3. 同一ユーザー内で同名タグが存在するか確認
     * 4. タグを新規作成
     * 5. レスポンス返却
     *
     * @param request タグ作成リクエスト
     * @return タグ作成レスポンス
     * @throws ConflictException 同名タグが既に存在する場合
     * @throws ResourceNotFoundException ユーザーが存在しない場合
     */
    @Transactional
    public TagCreateResponse create(TagCreateRequest request) {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. タグ名の前後空白を除去（trim）
        String trimmedName = request.getName().trim();

        // 3. タグ重複チェック
        if (tagRepository.findByUserIdAndName(userId, trimmedName).isPresent()) {
            throw new ConflictException("同じ名前のタグが既に存在します");
        }

        // ユーザー取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 4. タグを新規作成
        Tag tag = Tag.builder()
                .user(user)
                .name(trimmedName)
                .build();

        Tag saved = tagRepository.save(tag);

        // 5. レスポンス返却
        return TagCreateResponse.builder()
                .id(saved.getId())
                .build();
    }
}
