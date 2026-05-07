package com.example.studytracker.service;

import com.example.studytracker.dto.calendar.CalendarResponse;
import com.example.studytracker.exception.BadRequestException;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.security.CurrentUserProvider;
import com.example.studytracker.util.DateValidator;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * CalendarServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CalendarService 単体テスト")
class CalendarServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @Mock
    private DateValidator dateValidator;

    @InjectMocks
    private CalendarService calendarService;

    @Nested
    @DisplayName("getCalendar メソッドのテスト")
    class GetCalendarTests {

        @Test
        @DisplayName("正常系：yearとmonthを指定した場合、カレンダーを取得できる（データあり）")
        void getCalendar_Success_WithYearMonth() {
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
            List<CalendarResponse> result = calendarService.getCalendar(2024, 1);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(result.get(0).getTotalStudyMinutes()).isEqualTo(120L);
            assertThat(result.get(1).getDate()).isEqualTo(LocalDate.of(2024, 1, 16));
            assertThat(result.get(1).getTotalStudyMinutes()).isEqualTo(90L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 1);
            verify(studyRecordRepository, times(1))
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("正常系：yearとmonthを未指定の場合、現在年月でカレンダーを取得できる")
        void getCalendar_Success_WithoutYearMonth() {
            // モックの設定：日別集計結果
            StudyRecordRepository.DailyStudySummary summary = mock(StudyRecordRepository.DailyStudySummary.class);
            when(summary.getDate()).thenReturn(LocalDate.now());
            when(summary.getTotalStudyMinutes()).thenReturn(60L);

            List<StudyRecordRepository.DailyStudySummary> summaries = List.of(summary);

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(
                    anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(summaries);

            // 実行
            List<CalendarResponse> result = calendarService.getCalendar(null, null);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, never()).validateYearMonth(anyInt(), anyInt());
            verify(studyRecordRepository, times(1))
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("正常系：データがない場合、空リストを返却する")
        void getCalendar_Success_NoData() {
            // モックの設定：空リスト
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findDailyStudySummaryByUserIdAndDateRange(
                    anyLong(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            // 実行
            List<CalendarResponse> result = calendarService.getCalendar(2024, 1);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 1);
            verify(studyRecordRepository, times(1))
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("異常系：yearのみ指定した場合、BadRequestExceptionがスローされる")
        void getCalendar_YearOnly_ThrowsBadRequestException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);

            // 実行・検証
            assertThatThrownBy(() -> calendarService.getCalendar(2024, null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearとmonthは両方指定するか、両方未指定にしてください");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, never()).validateYearMonth(anyInt(), anyInt());
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("異常系：monthのみ指定した場合、BadRequestExceptionがスローされる")
        void getCalendar_MonthOnly_ThrowsBadRequestException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);

            // 実行・検証
            assertThatThrownBy(() -> calendarService.getCalendar(null, 1))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearとmonthは両方指定するか、両方未指定にしてください");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, never()).validateYearMonth(anyInt(), anyInt());
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("異常系：yearが範囲外の場合、BadRequestExceptionがスローされる")
        void getCalendar_InvalidYear_ThrowsBadRequestException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            doThrow(new BadRequestException("yearは2000年〜2027年の範囲で指定してください"))
                    .when(dateValidator).validateYearMonth(1999, 1);

            // 実行・検証
            assertThatThrownBy(() -> calendarService.getCalendar(1999, 1))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearは2000年〜2027年の範囲で指定してください");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(1999, 1);
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("異常系：monthが範囲外の場合、BadRequestExceptionがスローされる")
        void getCalendar_InvalidMonth_ThrowsBadRequestException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            doThrow(new BadRequestException("monthは1〜12の範囲で指定してください"))
                    .when(dateValidator).validateYearMonth(2024, 13);

            // 実行・検証
            assertThatThrownBy(() -> calendarService.getCalendar(2024, 13))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("monthは1〜12の範囲で指定してください");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 13);
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getCalendar_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> calendarService.getCalendar(2024, 1))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, never()).validateYearMonth(anyInt(), anyInt());
            verify(studyRecordRepository, never())
                    .findDailyStudySummaryByUserIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class));
        }
    }
}
