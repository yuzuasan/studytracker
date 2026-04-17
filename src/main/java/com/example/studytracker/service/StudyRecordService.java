package com.example.studytracker.service;

import com.example.studytracker.dto.studyrecord.StudyRecordCreateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDeleteResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDetailResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordListResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateResponse;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.BadRequestException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学習記録関連のビジネスロジックを担当するServiceクラス
 */
@Service
@RequiredArgsConstructor
public class StudyRecordService {

    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * 学習記録を登録する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. userIdでユーザー取得
     * 3. Entity生成
     * 4. 保存
     * 5. レスポンス返却
     *
     * @param request 学習記録登録リクエスト
     * @return 学習記録登録レスポンス
     * @throws ResourceNotFoundException ユーザーが存在しない場合
     */
    @Transactional
    public StudyRecordCreateResponse create(StudyRecordCreateRequest request) {
        // 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // userIdでユーザーを取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));

        // 入力値の前後空白を除去
        String trimmedSubject = request.getSubject().trim();
        String trimmedMemo = request.getMemo() != null
                ? request.getMemo().trim()
                : null;

        // Entityを生成
        StudyRecord studyRecord = StudyRecord.builder()
                .user(user)
                .studyDate(request.getDate())
                .subject(trimmedSubject)
                .studyMinutes(request.getStudyMinutes())
                .memo(trimmedMemo)
                .build();

        // 保存（createdAt/updatedAtは@PrePersistで自動設定）
        StudyRecord saved = studyRecordRepository.save(studyRecord);

        // レスポンスを返却
        return StudyRecordCreateResponse.builder()
                .id(saved.getId())
                .build();
    }

    /**
     * 学習記録一覧を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. userIdで検索（作成日時降順）
     * 3. EntityリストをDTOに変換
     * 4. レスポンス返却
     *
     * @return 学習記録一覧レスポンス
     */
    @Transactional(readOnly = true)
    public StudyRecordListResponse findAll() {
        // 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // userIdで学習記録を取得（作成日時降順）
        // 認可制御：他ユーザーのデータは取得できない
        List<StudyRecord> studyRecords = studyRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // EntityをDTOに変換
        List<StudyRecordListResponse.StudyRecordSummary> summaries = studyRecords.stream()
                .map(this::toSummary)
                .toList();

        // レスポンスを返却
        return StudyRecordListResponse.builder()
                .records(summaries)
                .build();
    }

    /**
     * StudyRecord EntityをStudyRecordSummary DTOに変換する
     *
     * @param studyRecord 学習記録Entity
     * @return 学習記録サマリーDTO
     */
    private StudyRecordListResponse.StudyRecordSummary toSummary(StudyRecord studyRecord) {
        return StudyRecordListResponse.StudyRecordSummary.builder()
                .id(studyRecord.getId())
                .date(studyRecord.getStudyDate())
                .subject(studyRecord.getSubject())
                .studyMinutes(studyRecord.getStudyMinutes())
                .build();
    }

    /**
     * 学習記録詳細を取得する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. findByIdAndUserIdで検索
     * 3. EntityをDTOに変換
     * 4. レスポンス返却
     *
     * @param id 学習記録ID
     * @return 学習記録詳細レスポンス
     * @throws ResourceNotFoundException データが存在しない場合
     */
    @Transactional(readOnly = true)
    public StudyRecordDetailResponse findById(Long id) {
        // 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // findByIdAndUserIdで検索（認可制御）
        StudyRecord studyRecord = studyRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("学習記録が見つかりません"));

        // EntityをDTOに変換
        return toDetailResponse(studyRecord);
    }

    /**
     * StudyRecord EntityをStudyRecordDetailResponse DTOに変換する
     *
     * @param studyRecord 学習記録Entity
     * @return 学習記録詳細レスポンスDTO
     */
    private StudyRecordDetailResponse toDetailResponse(StudyRecord studyRecord) {
        return StudyRecordDetailResponse.builder()
                .id(studyRecord.getId())
                .date(studyRecord.getStudyDate())
                .subject(studyRecord.getSubject())
                .studyMinutes(studyRecord.getStudyMinutes())
                .memo(studyRecord.getMemo())
                .tags(List.of()) // Phase1ではタグ未実装のため空リストを返却
                .build();
    }

    /**
     * 学習記録を部分更新する
     *
     * 処理フロー:
     * 1. リクエストバリデーション（全項目nullチェック）
     * 2. 認証情報からuserId取得
     * 3. findByIdAndUserIdで検索（認可制御）
     * 4. nullでない項目のみ更新
     * 5. 保存（updatedAtは@PreUpdateで自動更新）
     * 6. レスポンス返却
     *
     * @param id 学習記録ID
     * @param request 学習記録更新リクエスト
     * @return 学習記録更新レスポンス
     * @throws BadRequestException 全項目がnullの場合
     * @throws ResourceNotFoundException データが存在しない場合
     */
    @Transactional
    public StudyRecordUpdateResponse update(Long id, StudyRecordUpdateRequest request) {
        // 1. リクエストバリデーション（全項目nullチェック）
        if (request.getDate() == null &&
            request.getSubject() == null &&
            request.getStudyMinutes() == null &&
            request.getMemo() == null) {
            throw new BadRequestException("少なくとも1項目は更新項目として指定してください");
        }

        // 2. 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // 3. findByIdAndUserIdで検索（認可制御：自分のデータのみ更新可能）
        StudyRecord studyRecord = studyRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("学習記録が見つかりません"));

        // 4. nullでない項目のみ更新（文字列は前後空白を除去）
        if (request.getDate() != null) {
            studyRecord.setStudyDate(request.getDate());
        }
        if (request.getSubject() != null) {
            studyRecord.setSubject(request.getSubject().trim());
        }
        if (request.getStudyMinutes() != null) {
            studyRecord.setStudyMinutes(request.getStudyMinutes());
        }
        if (request.getMemo() != null) {
            studyRecord.setMemo(request.getMemo().trim());
        }

        // 5. 保存（updatedAtは@PreUpdateで自動更新される）
        StudyRecord saved = studyRecordRepository.save(studyRecord);

        // 6. レスポンスを返却
        return StudyRecordUpdateResponse.builder()
                .id(saved.getId())
                .build();
    }

    /**
     * 学習記録を削除する
     *
     * 処理フロー:
     * 1. 認証情報からuserId取得
     * 2. findByIdAndUserIdで検索（認可制御）
     * 3. 削除
     * 4. レスポンス返却
     *
     * @param id 学習記録ID
     * @return 学習記録削除レスポンス
     * @throws ResourceNotFoundException データが存在しない場合
     */
    @Transactional
    public StudyRecordDeleteResponse delete(Long id) {
        // 認証情報からuserIdを取得
        Long userId = currentUserProvider.getUserId();

        // findByIdAndUserIdで検索（認可制御：自分のデータのみ削除可能）
        StudyRecord studyRecord = studyRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("学習記録が見つかりません"));

        // 削除
        studyRecordRepository.delete(studyRecord);

        // レスポンスを返却
        return StudyRecordDeleteResponse.builder()
                .message("deleted")
                .build();
    }
}
