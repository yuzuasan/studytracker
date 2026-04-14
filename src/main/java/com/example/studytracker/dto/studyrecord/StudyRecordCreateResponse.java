package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 学習記録登録レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordCreateResponse {

    /**
     * 作成された学習記録のID
     */
    private Long id;
}
