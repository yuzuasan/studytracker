package com.example.studytracker.service;

import com.example.studytracker.dto.stat.DailyStatsRequest;
import com.example.studytracker.dto.stat.DailyStatsResponse;
import com.example.studytracker.dto.stat.MonthlyStatsResponse;
import com.example.studytracker.dto.stat.SubjectStatsResponse;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * StatisticsServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService 単体テスト")
class StatisticsServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private DailyStatsRequest dailyStatsRequest;

    @BeforeEach
    void setUp() {
        // 日別統計リクエストのセットアップ
        dailyStatsRequest = DailyStatsRequest.builder()
                .from(LocalDate.of(2024, 1, 1))
                .to(LocalDate.of(2024, 1, 31))
                .build();
    }

    @Nested
    @DisplayName("getDailyStats メソッドのテスト")
    class GetDailyStatsTests {

        @Test
        @DisplayName("正常系：日別統計を取得できる（データあり）")
        void getDailyStats_Success_WithData() {
            // モックの設定：日別集計結果
            StudyRecordRepository.DailyStudySummary summary1 = mock(StudyRecordRepository.DailyStudySummary.class);
            when(summary1.getDate()).thenReturn(LocalDate.of(2024, 1, 15));
            when(summary1.getTotalStudyMinutes()).thenReturn(120L);

            StudyRecordRepository.DailyStudySummary summary2 = mock(StudyRecordRepository.DailyStudySummary.class);
            when(summary2.getDate()).thenReturn(LocalDate.of(2024, 1, 16));
            when(summary2.getTotalStudyMinutes()).thenReturn(90L);

            List<StudyRecordRepository.DailyStudySummary> summaries = List.of(summary1, summary2);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(
                    anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(summaries);

            // 実行
            List<DailyStatsResponse> result = statisticsService.getDailyStats(dailyStatsRequest);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(result.get(0).getTotalStudyMinutes()).isEqualTo(120L);
            assertThat(result.get(1).getDate()).isEqualTo(LocalDate.of(2024, 1, 16));
            assertThat(result.get(1).getTotalStudyMinutes()).isEqualTo(90L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1))
                    .findDailyStudySummaryByUserIdAndDateRange(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("正常系：日別統計を取得できる（データなし）")
        void getDailyStats_Success_WithoutData() {
            // モックの設定：空リスト
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(
                    anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            // 実行
            List<DailyStatsResponse> result = statisticsService.getDailyStats(dailyStatsRequest);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1))
                    .findDailyStudySummaryByUserIdAndDateRange(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getDailyStats_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> statisticsService.getDailyStats(dailyStatsRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("getMonthlyStats メソッドのテスト")
    class GetMonthlyStatsTests {

        @Test
        @DisplayName("正常系：月別統計を取得できる（データあり）")
        void getMonthlyStats_Success_WithData() {
            // モックの設定：月別集計結果
            StudyRecordRepository.MonthlyStudySummary summary1 = mock(StudyRecordRepository.MonthlyStudySummary.class);
            when(summary1.getMonth()).thenReturn("2024-01");
            when(summary1.getTotalStudyMinutes()).thenReturn(3000L);

            StudyRecordRepository.MonthlyStudySummary summary2 = mock(StudyRecordRepository.MonthlyStudySummary.class);
            when(summary2.getMonth()).thenReturn("2024-02");
            when(summary2.getTotalStudyMinutes()).thenReturn(2500L);

            List<StudyRecordRepository.MonthlyStudySummary> summaries = List.of(summary1, summary2);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findMonthlyStudySummaryByUserId(anyLong()))
                    .thenReturn(summaries);

            // 実行
            List<MonthlyStatsResponse> result = statisticsService.getMonthlyStats();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getMonth()).isEqualTo("2024-01");
            assertThat(result.get(0).getTotalStudyMinutes()).isEqualTo(3000L);
            assertThat(result.get(1).getMonth()).isEqualTo("2024-02");
            assertThat(result.get(1).getTotalStudyMinutes()).isEqualTo(2500L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findMonthlyStudySummaryByUserId(1L);
        }

        @Test
        @DisplayName("正常系：月別統計を取得できる（データなし）")
        void getMonthlyStats_Success_WithoutData() {
            // モックの設定：空リスト
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findMonthlyStudySummaryByUserId(anyLong()))
                    .thenReturn(List.of());

            // 実行
            List<MonthlyStatsResponse> result = statisticsService.getMonthlyStats();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findMonthlyStudySummaryByUserId(1L);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getMonthlyStats_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> statisticsService.getMonthlyStats())
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findMonthlyStudySummaryByUserId(anyLong());
        }
    }

    @Nested
    @DisplayName("getSubjectStats メソッドのテスト")
    class GetSubjectStatsTests {

        @Test
        @DisplayName("正常系：科目別統計を取得できる（データあり）")
        void getSubjectStats_Success_WithData() {
            // モックの設定：科目別集計結果
            StudyRecordRepository.SubjectStudySummary summary1 = mock(StudyRecordRepository.SubjectStudySummary.class);
            when(summary1.getSubject()).thenReturn("Java");
            when(summary1.getTotalStudyMinutes()).thenReturn(1500L);

            StudyRecordRepository.SubjectStudySummary summary2 = mock(StudyRecordRepository.SubjectStudySummary.class);
            when(summary2.getSubject()).thenReturn("Python");
            when(summary2.getTotalStudyMinutes()).thenReturn(1000L);

            List<StudyRecordRepository.SubjectStudySummary> summaries = List.of(summary1, summary2);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findSubjectStudySummaryByUserId(anyLong()))
                    .thenReturn(summaries);

            // 実行
            List<SubjectStatsResponse> result = statisticsService.getSubjectStats();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSubject()).isEqualTo("Java");
            assertThat(result.get(0).getTotalStudyMinutes()).isEqualTo(1500L);
            assertThat(result.get(1).getSubject()).isEqualTo("Python");
            assertThat(result.get(1).getTotalStudyMinutes()).isEqualTo(1000L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findSubjectStudySummaryByUserId(1L);
        }

        @Test
        @DisplayName("正常系：科目別統計を取得できる（データなし）")
        void getSubjectStats_Success_WithoutData() {
            // モックの設定：空リスト
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findSubjectStudySummaryByUserId(anyLong()))
                    .thenReturn(List.of());

            // 実行
            List<SubjectStatsResponse> result = statisticsService.getSubjectStats();

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findSubjectStudySummaryByUserId(1L);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getSubjectStats_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> statisticsService.getSubjectStats())
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findSubjectStudySummaryByUserId(anyLong());
        }
    }
}
