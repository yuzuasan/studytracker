package com.example.studytracker.service;

import com.example.studytracker.dto.tag.TagCreateRequest;
import com.example.studytracker.dto.tag.TagCreateResponse;
import com.example.studytracker.dto.tag.TagDeleteResponse;
import com.example.studytracker.dto.tag.TagListResponse;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.Tag;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.TagRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * タグ関連のビジネスロジックを担当するServiceクラス
 */
@Slf4j
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
     * 2. 同一ユーザー内で同名タグが存在するか確認
     * 3. タグを新規作成
     * 4. レスポンス返却
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

        // 2. タグ重複チェック
        if (tagRepository.findByUserIdAndName(userId, request.getName()).isPresent()) {
            throw new ConflictException("同じ名前のタグが既に存在します");
        }

        // ユーザー取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 3. タグを新規作成
        Tag tag = Tag.builder()
                .user(user)
                .name(request.getName())
                .build();

        Tag saved = tagRepository.save(tag);

        log.debug("[{}] create result: id={}", this.getClass().getSimpleName(), saved.getId());

        // 5. レスポンス返却
        return TagCreateResponse.builder()
                .id(saved.getId())
                .build();
    }

    /**
     * タグ一覧を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. ユーザーIDに紐づくタグ一覧を取得
     * 3. レスポンスに変換
     * 4. 返却
     *
     * @return タグ一覧レスポンス
     */
    @Transactional(readOnly = true)
    public TagListResponse getAll() {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. ユーザーIDに紐づくタグ一覧を取得
        List<Tag> tags = tagRepository.findByUserIdOrderByNameAsc(userId);

        // 3. レスポンスに変換
        List<TagListResponse.TagResponse> tagResponses = tags.stream()
                .map(tag -> TagListResponse.TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build())
                .toList();

        // 4. 返却
        TagListResponse result = TagListResponse.builder()
                .tags(tagResponses)
                .build();

        log.debug("[{}] getAll result: count={}", this.getClass().getSimpleName(), tagResponses.size());
        return result;
    }

    /**
     * タグを削除する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. タグ存在チェック（id + userIdで検索）
     * 3. タグと学習記録の関連を解除
     * 4. タグを削除
     * 5. レスポンス返却
     *
     * @param id タグID
     * @return タグ削除レスポンス
     * @throws ResourceNotFoundException タグが存在しない場合
     */
    @Transactional
    public TagDeleteResponse delete(Long id) {
        // 1. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 2. タグ存在チェック（id + userIdで検索して所有権も確認）
        Tag tag = tagRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("タグが見つかりません"));

        // 3. タグと学習記録の関連を解除（中間テーブルのレコード削除）
        if (tag.getStudyRecords() != null && !tag.getStudyRecords().isEmpty()) {
            for (StudyRecord studyRecord : new ArrayList<>(tag.getStudyRecords())) {
                studyRecord.getTags().remove(tag);
            }
        }

        // 4. タグを削除
        tagRepository.delete(tag);

        log.debug("[{}] delete result: id={}", this.getClass().getSimpleName(), id);

        // 5. レスポンス返却
        return TagDeleteResponse.builder()
                .message("deleted")
                .build();
    }
}
