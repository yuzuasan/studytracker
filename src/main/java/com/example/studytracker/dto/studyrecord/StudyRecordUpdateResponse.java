package com.example.studytracker.dto.studyrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 学習記録更新レスポンスDTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRecordUpdateResponse {

    /**
     * 更新された学習記録のID
     */
    private Long id;
}
