package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 学習記録削除レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordDeleteResponse {

    /**
     * 削除完了メッセージ
     */
    private String message;
}
