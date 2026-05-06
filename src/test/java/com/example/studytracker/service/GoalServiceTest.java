package com.example.studytracker.service;

import com.example.studytracker.dto.goal.GoalCreateRequest;
import com.example.studytracker.dto.goal.GoalCreateResponse;
import com.example.studytracker.dto.goal.GoalDeleteResponse;
import com.example.studytracker.dto.goal.GoalListResponse;
import com.example.studytracker.dto.goal.GoalUpdateRequest;
import com.example.studytracker.dto.goal.GoalUpdateResponse;
import com.example.studytracker.entity.Goal;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.BadRequestException;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.GoalRepository;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import com.example.studytracker.util.DateValidator;
import com.example.studytracker.testutil.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * GoalServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GoalService 単体テスト")
class GoalServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @Mock
    private DateValidator dateValidator;

    @InjectMocks
    private GoalService goalService;

    private User mockUser;
    private Goal mockGoal;
    private GoalCreateRequest createRequest;
    private GoalUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // テスト用ユーザーデータのセットアップ
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .githubUsername("githubuser")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // テスト用目標データのセットアップ
        mockGoal = Goal.builder()
                .id(1L)
                .user(mockUser)
                .targetMonth("2024-01")
                .targetMinutes(3000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 作成リクエストのセットアップ
        createRequest = new GoalCreateRequest();
        createRequest.setMonth("2024-01");
        TestUtil.setField(createRequest, "targetMinutes", 3000);

        // 更新リクエストのセットアップ
        updateRequest = new GoalUpdateRequest();
        TestUtil.setField(updateRequest, "targetMinutes", 4000);
    }

    @Nested
    @DisplayName("create メソッドのテスト")
    class CreateTests {

        @Test
        @DisplayName("正常系：目標の作成が成功する")
        void create_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdAndTargetMonth(1L, "2024-01"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);

            // 実行
            GoalCreateResponse response = goalService.create(createRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 1);
            verify(goalRepository, times(1)).findByUserIdAndTargetMonth(1L, "2024-01");
            verify(userRepository, times(1)).findById(1L);
            verify(goalRepository, times(1)).save(any(Goal.class));
        }

        @Test
        @DisplayName("異常系：同一月の目標が既に存在する場合、ConflictExceptionがスローされる")
        void create_DuplicateMonth_ThrowsConflictException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdAndTargetMonth(1L, "2024-01"))
                    .thenReturn(Optional.of(mockGoal));

            // 実行・検証
            assertThatThrownBy(() -> goalService.create(createRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("指定された月の目標が既に存在します");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 1);
            verify(goalRepository, times(1)).findByUserIdAndTargetMonth(1L, "2024-01");
            verify(userRepository, never()).findById(any());
            verify(goalRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：ユーザーが存在しない場合、ResourceNotFoundExceptionがスローされる")
        void create_UserNotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdAndTargetMonth(1L, "2024-01"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> goalService.create(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, times(1)).validateYearMonth(2024, 1);
            verify(goalRepository, times(1)).findByUserIdAndTargetMonth(1L, "2024-01");
            verify(userRepository, times(1)).findById(1L);
            verify(goalRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void create_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> goalService.create(createRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(dateValidator, never()).validateYearMonth(anyInt(), anyInt());
            verify(goalRepository, never()).findByUserIdAndTargetMonth(anyLong(), any());
            verify(goalRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：年が範囲外の場合、BadRequestExceptionがスローされる")
        void create_InvalidYear_ThrowsBadRequestException() {
            // 範囲外の年を設定
            GoalCreateRequest invalidRequest = new GoalCreateRequest();
            invalidRequest.setMonth("1999-01");
            TestUtil.setField(invalidRequest, "targetMinutes", 3000);

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            doThrow(new BadRequestException("yearは2000年〜2027年の範囲で指定してください"))
                    .when(dateValidator).validateYearMonth(1999, 1);

            // 実行・検証
            assertThatThrownBy(() -> goalService.create(invalidRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearは2000年〜2027年の範囲で指定してください");

            // モックの呼び出し検証
            verify(dateValidator, times(1)).validateYearMonth(1999, 1);
            verify(goalRepository, never()).findByUserIdAndTargetMonth(anyLong(), any());
            verify(goalRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：月が範囲外の場合、BadRequestExceptionがスローされる")
        void create_InvalidMonth_ThrowsBadRequestException() {
            // 範囲外の月を設定
            GoalCreateRequest invalidRequest = new GoalCreateRequest();
            invalidRequest.setMonth("2024-13");
            TestUtil.setField(invalidRequest, "targetMinutes", 3000);

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            doThrow(new BadRequestException("monthは1〜12の範囲で指定してください"))
                    .when(dateValidator).validateYearMonth(2024, 13);

            // 実行・検証
            assertThatThrownBy(() -> goalService.create(invalidRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("monthは1〜12の範囲で指定してください");

            // モックの呼び出し検証
            verify(dateValidator, times(1)).validateYearMonth(2024, 13);
            verify(goalRepository, never()).findByUserIdAndTargetMonth(anyLong(), any());
            verify(goalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getAllGoals メソッドのテスト")
    class GetAllGoalsTests {

        @Test
        @DisplayName("正常系：目標一覧を取得できる（データあり）")
        void getAllGoals_Success_WithData() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdOrderByTargetMonthDesc(1L))
                    .thenReturn(List.of(mockGoal));
            when(studyRecordRepository.findTotalStudyMinutesByUserIdAndDateRange(
                    anyLong(), any(), any()))
                    .thenReturn(1500L);

            // 実行
            GoalListResponse response = goalService.getAllGoals();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getGoals()).hasSize(1);
            assertThat(response.getGoals().get(0).getId()).isEqualTo(1L);
            assertThat(response.getGoals().get(0).getMonth()).isEqualTo("2024-01");
            assertThat(response.getGoals().get(0).getTargetMinutes()).isEqualTo(3000);
            assertThat(response.getGoals().get(0).getAchievedMinutes()).isEqualTo(1500);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByUserIdOrderByTargetMonthDesc(1L);
            verify(studyRecordRepository, times(1))
                    .findTotalStudyMinutesByUserIdAndDateRange(anyLong(), any(), any());
        }

        @Test
        @DisplayName("正常系：目標一覧を取得できる（データなし）")
        void getAllGoals_Success_WithoutData() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdOrderByTargetMonthDesc(1L))
                    .thenReturn(List.of());

            // 実行
            GoalListResponse response = goalService.getAllGoals();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getGoals()).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByUserIdOrderByTargetMonthDesc(1L);
            verify(studyRecordRepository, never())
                    .findTotalStudyMinutesByUserIdAndDateRange(anyLong(), any(), any());
        }

        @Test
        @DisplayName("正常系：学習記録がない場合、達成時間は0として扱われる")
        void getAllGoals_Success_NoStudyRecords() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByUserIdOrderByTargetMonthDesc(1L))
                    .thenReturn(List.of(mockGoal));
            when(studyRecordRepository.findTotalStudyMinutesByUserIdAndDateRange(
                    anyLong(), any(), any()))
                    .thenReturn(null);

            // 実行
            GoalListResponse response = goalService.getAllGoals();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getGoals()).hasSize(1);
            assertThat(response.getGoals().get(0).getAchievedMinutes()).isEqualTo(0);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByUserIdOrderByTargetMonthDesc(1L);
            verify(studyRecordRepository, times(1))
                    .findTotalStudyMinutesByUserIdAndDateRange(anyLong(), any(), any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void getAllGoals_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> goalService.getAllGoals())
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, never()).findByUserIdOrderByTargetMonthDesc(anyLong());
            verify(studyRecordRepository, never())
                    .findTotalStudyMinutesByUserIdAndDateRange(anyLong(), any(), any());
        }
    }

    @Nested
    @DisplayName("update メソッドのテスト")
    class UpdateTests {

        @Test
        @DisplayName("正常系：目標を更新できる")
        void update_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockGoal));
            when(goalRepository.save(any(Goal.class))).thenReturn(mockGoal);

            // 実行
            GoalUpdateResponse response = goalService.update(1L, updateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(goalRepository, times(1)).save(any(Goal.class));
        }

        @Test
        @DisplayName("異常系：目標が存在しない場合、ResourceNotFoundExceptionがスローされる")
        void update_NotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> goalService.update(1L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("目標が見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(goalRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void update_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> goalService.update(1L, updateRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(goalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete メソッドのテスト")
    class DeleteTests {

        @Test
        @DisplayName("正常系：目標を削除できる")
        void delete_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockGoal));
            doNothing().when(goalRepository).delete(mockGoal);

            // 実行
            GoalDeleteResponse response = goalService.delete(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("deleted");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(goalRepository, times(1)).delete(mockGoal);
        }

        @Test
        @DisplayName("異常系：目標が存在しない場合、ResourceNotFoundExceptionがスローされる")
        void delete_NotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(goalRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> goalService.delete(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("目標が見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(goalRepository, never()).delete(any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void delete_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> goalService.delete(1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(goalRepository, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(goalRepository, never()).delete(any());
        }
    }
}
