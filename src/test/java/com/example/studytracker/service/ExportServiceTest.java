package com.example.studytracker.service;

import com.example.studytracker.dto.export.ExportCsvRequest;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.User;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ExportServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExportService 単体テスト")
class ExportServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @InjectMocks
    private ExportService exportService;

    @Nested
    @DisplayName("exportToCsv メソッドのテスト")
    class ExportToCsvTests {

        @Test
        @DisplayName("正常系：学習記録がある場合、CSV形式で出力する")
        void exportToCsv_Success_WithRecords() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .build();

            StudyRecord record1 = StudyRecord.builder()
                    .id(1L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 1))
                    .subject("数学")
                    .studyMinutes(60)
                    .memo("復習")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            StudyRecord record2 = StudyRecord.builder()
                    .id(2L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 2))
                    .subject("英語")
                    .studyMinutes(90)
                    .memo("リスニング")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<StudyRecord> studyRecords = List.of(record1, record2);

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).contains("date,subject,minutes,memo");
            assertThat(result).contains("2024-01-01,数学,60,復習");
            assertThat(result).contains("2024-01-02,英語,90,リスニング");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：学習記録がない場合、ヘッダのみ出力する")
        void exportToCsv_Success_NoRecords() {
            // モックの設定
            Long userId = 1L;
            List<StudyRecord> studyRecords = List.of();

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo("date,subject,minutes,memo\n");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：カンマを含むフィールドをエスケープする")
        void exportToCsv_Success_EscapeComma() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .build();

            StudyRecord record = StudyRecord.builder()
                    .id(1L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 1))
                    .subject("数学,英語")
                    .studyMinutes(60)
                    .memo("復習,予習")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<StudyRecord> studyRecords = List.of(record);

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).contains("\"数学,英語\"");
            assertThat(result).contains("\"復習,予習\"");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：改行を含むフィールドをエスケープする")
        void exportToCsv_Success_EscapeNewline() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .build();

            StudyRecord record = StudyRecord.builder()
                    .id(1L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 1))
                    .subject("数学")
                    .studyMinutes(60)
                    .memo("復習\n予習")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<StudyRecord> studyRecords = List.of(record);

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).contains("\"復習\n予習\"");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：ダブルクォートを含むフィールドをエスケープする")
        void exportToCsv_Success_EscapeDoubleQuote() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .build();

            StudyRecord record = StudyRecord.builder()
                    .id(1L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 1))
                    .subject("数学")
                    .studyMinutes(60)
                    .memo("復習\"予習")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<StudyRecord> studyRecords = List.of(record);

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).contains("\"復習\"\"予習\"");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：nullのフィールドを空文字にする")
        void exportToCsv_Success_NullField() {
            // モックの設定
            Long userId = 1L;
            User user = User.builder()
                    .id(userId)
                    .username("testuser")
                    .password("password")
                    .build();

            StudyRecord record = StudyRecord.builder()
                    .id(1L)
                    .user(user)
                    .studyDate(LocalDate.of(2024, 1, 1))
                    .subject("数学")
                    .studyMinutes(60)
                    .memo(null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<StudyRecord> studyRecords = List.of(record);

            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenReturn(userId);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(studyRecords);

            // 実行
            String result = exportService.exportToCsv(request);

            // 検証
            assertThat(result).isNotNull();
            assertThat(result).contains("2024-01-01,数学,60,");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void exportToCsv_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            ExportCsvRequest request = ExportCsvRequest.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> exportService.exportToCsv(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findAll(any(Specification.class), any(Sort.class));
        }
    }
}
