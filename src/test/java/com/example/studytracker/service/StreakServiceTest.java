package com.example.studytracker.service;

import com.example.studytracker.dto.streak.StreakResponse;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * StreakServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StreakService 単体テスト")
class StreakServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @InjectMocks
    private StreakService streakService;

    @Nested
    @DisplayName("getCurrentStreak メソッドのテスト")
    class GetCurrentStreakTests {

        @Test
        @DisplayName("正常系：ストリークがある場合、連続学習日数を返却する")
        void getCurrentStreak_Success_WithStreak() {
            // モックの設定：今日、昨日、一昨日と連続学習
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalDate dayBeforeYesterday = today.minusDays(2);
            List<LocalDate> studyDates = List.of(today, yesterday, dayBeforeYesterday);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDistinctStudyDatesByUserIdOrderByDateDesc(1L))
                    .thenReturn(studyDates);

            // 実行
            StreakResponse result = streakService.getCurrentStreak();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getCurrentStreak()).isEqualTo(3);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findDistinctStudyDatesByUserIdOrderByDateDesc(1L);
        }

        @Test
        @DisplayName("正常系：ストリークが0の場合、0を返却する")
        void getCurrentStreak_Success_ZeroStreak() {
            // モックの設定：今日学習していない
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            List<LocalDate> studyDates = List.of(yesterday);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDistinctStudyDatesByUserIdOrderByDateDesc(1L))
                    .thenReturn(studyDates);

            // 実行
            StreakResponse result = streakService.getCurrentStreak();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getCurrentStreak()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findDistinctStudyDatesByUserIdOrderByDateDesc(1L);
        }

        @Test
        @DisplayName("正常系：学習記録がない場合、0を返却する")
        void getCurrentStreak_Success_NoData() {
            // モックの設定：学習記録なし
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDistinctStudyDatesByUserIdOrderByDateDesc(1L))
                    .thenReturn(List.of());

            // 実行
            StreakResponse result = streakService.getCurrentStreak();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result.getCurrentStreak()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findDistinctStudyDatesByUserIdOrderByDateDesc(1L);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getCurrentStreak_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> streakService.getCurrentStreak())
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findDistinctStudyDatesByUserIdOrderByDateDesc(anyLong());
        }
    }
}
