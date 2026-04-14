package com.example.studytracker.service;

import com.example.studytracker.dto.studyrecord.StudyRecordCreateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateResponse;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
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

        // Entityを生成
        StudyRecord studyRecord = StudyRecord.builder()
                .user(user)
                .studyDate(request.getDate())
                .subject(request.getSubject())
                .studyMinutes(request.getStudyMinutes())
                .memo(request.getMemo())
                .build();

        // 保存（createdAt/updatedAtは@PrePersistで自動設定）
        StudyRecord saved = studyRecordRepository.save(studyRecord);

        // レスポンスを返却
        return StudyRecordCreateResponse.builder()
                .id(saved.getId())
                .build();
    }
}
