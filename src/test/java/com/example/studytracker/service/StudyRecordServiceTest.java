package com.example.studytracker.service;

import com.example.studytracker.dto.studyrecord.StudyRecordCreateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDeleteResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDetailResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordListResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordSearchCondition;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateResponse;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.Tag;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.BadRequestException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.exception.UnauthorizedException;
import com.example.studytracker.repository.StudyRecordRepository;
import com.example.studytracker.repository.TagRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * StudyRecordServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudyRecordService 単体テスト")
class StudyRecordServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyRecordRepository studyRecordRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private DateValidator dateValidator;

    @InjectMocks
    private StudyRecordService studyRecordService;

    private User mockUser;
    private StudyRecord mockStudyRecord;
    private StudyRecordCreateRequest createRequest;
    private StudyRecordUpdateRequest updateRequest;

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

        // テスト用学習記録データのセットアップ
        mockStudyRecord = StudyRecord.builder()
                .id(1L)
                .user(mockUser)
                .studyDate(LocalDate.of(2024, 1, 15))
                .subject("Java")
                .studyMinutes(60)
                .memo("Javaの基礎を学習")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 登録リクエストのセットアップ
        createRequest = new StudyRecordCreateRequest();
        TestUtil.setField(createRequest, "date", LocalDate.of(2024, 1, 15));
        TestUtil.setField(createRequest, "subject", "Java");
        TestUtil.setField(createRequest, "studyMinutes", 60);
        TestUtil.setField(createRequest, "memo", "Javaの基礎を学習");

        // 更新リクエストのセットアップ
        updateRequest = new StudyRecordUpdateRequest();
        TestUtil.setField(updateRequest, "subject", "Python");
    }

    @Nested
    @DisplayName("create メソッドのテスト")
    class CreateTests {

        @Test
        @DisplayName("正常系：学習記録の登録が成功する（タグなし）")
        void create_Success_WithoutTags() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordCreateResponse response = studyRecordService.create(createRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(1L);
            verify(dateValidator, times(1)).validateYear(2024);
            verify(studyRecordRepository, times(1)).save(any(StudyRecord.class));
        }

        @Test
        @DisplayName("正常系：学習記録の登録が成功する（タグあり・既存タグ）")
        void create_Success_WithExistingTags() {
            // タグを設定
            createRequest.setTags(List.of("Java", "Spring"));

            // 既存タグのモック
            Tag existingTag1 = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Java")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            Tag existingTag2 = Tag.builder()
                    .id(2L)
                    .user(mockUser)
                    .name("Spring")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(tagRepository.findByUserIdAndNameIn(1L, List.of("Java", "Spring")))
                    .thenReturn(List.of(existingTag1, existingTag2));
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordCreateResponse response = studyRecordService.create(createRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(tagRepository, times(1)).findByUserIdAndNameIn(1L, List.of("Java", "Spring"));
            verify(tagRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("正常系：学習記録の登録が成功する（タグあり・新規タグ作成）")
        void create_Success_WithNewTags() {
            // タグを設定
            createRequest.setTags(List.of("Java"));

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(tagRepository.findByUserIdAndNameIn(1L, List.of("Java"))).thenReturn(List.of());
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordCreateResponse response = studyRecordService.create(createRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(tagRepository, times(1)).findByUserIdAndNameIn(1L, List.of("Java"));
            verify(tagRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("異常系：ユーザーが存在しない場合、ResourceNotFoundExceptionがスローされる")
        void create_UserNotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.create(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, times(1)).findById(1L);
            verify(studyRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void create_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.create(createRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(userRepository, never()).findById(any());
            verify(studyRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：学習日が範囲外の場合、BadRequestExceptionがスローされる")
        void create_InvalidYear_ThrowsBadRequestException() {
            // 範囲外の年を設定
            StudyRecordCreateRequest invalidRequest = new StudyRecordCreateRequest();
            TestUtil.setField(invalidRequest, "date", LocalDate.of(1999, 1, 1));
            TestUtil.setField(invalidRequest, "subject", "Java");
            TestUtil.setField(invalidRequest, "studyMinutes", 60);

            // モックの設定
            doThrow(new BadRequestException("yearは2000年〜2027年の範囲で指定してください"))
                    .when(dateValidator).validateYear(1999);

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.create(invalidRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearは2000年〜2027年の範囲で指定してください");

            // モックの呼び出し検証
            verify(dateValidator, times(1)).validateYear(1999);
            verify(currentUserProvider, never()).getUserId();
            verify(studyRecordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll メソッドのテスト")
    class FindAllTests {

        @Test
        @DisplayName("正常系：検索条件なしで全件取得できる")
        void findAll_Success_WithoutCondition() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(mockStudyRecord));

            // 実行
            StudyRecordListResponse response = studyRecordService.findAll(null);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getRecords()).hasSize(1);
            assertThat(response.getRecords().get(0).getId()).isEqualTo(1L);
            assertThat(response.getRecords().get(0).getSubject()).isEqualTo("Java");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：検索条件付きで取得できる")
        void findAll_Success_WithCondition() {
            // 検索条件を設定
            StudyRecordSearchCondition condition = StudyRecordSearchCondition.builder()
                    .from(LocalDate.of(2024, 1, 1))
                    .to(LocalDate.of(2024, 1, 31))
                    .build();

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(mockStudyRecord));

            // 実行
            StudyRecordListResponse response = studyRecordService.findAll(condition);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getRecords()).hasSize(1);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：検索結果が空の場合でも正しく動作する")
        void findAll_Success_EmptyResult() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of());

            // 実行
            StudyRecordListResponse response = studyRecordService.findAll(null);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getRecords()).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findAll(any(Specification.class), any(Sort.class));
        }

        @Test
        @DisplayName("正常系：タグ付きの学習記録を正しくDTO変換できる")
        void findAll_Success_WithTags() {
            // タグ付きの学習記録を作成
            Tag tag = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Java")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            mockStudyRecord.setTags(List.of(tag));

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findAll(any(Specification.class), any(Sort.class)))
                    .thenReturn(List.of(mockStudyRecord));

            // 実行
            StudyRecordListResponse response = studyRecordService.findAll(null);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getRecords()).hasSize(1);
            assertThat(response.getRecords().get(0).getTags()).containsExactly("Java");
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void findAll_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.findAll(null))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findAll(any(Specification.class), any(Sort.class));
        }
    }

    @Nested
    @DisplayName("findById メソッドのテスト")
    class FindByIdTests {

        @Test
        @DisplayName("正常系：IDで学習記録を取得できる")
        void findById_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));

            // 実行
            StudyRecordDetailResponse response = studyRecordService.findById(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSubject()).isEqualTo("Java");
            assertThat(response.getStudyMinutes()).isEqualTo(60);
            assertThat(response.getMemo()).isEqualTo("Javaの基礎を学習");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
        }

        @Test
        @DisplayName("正常系：タグ付きの学習記録を正しくDTO変換できる")
        void findById_Success_WithTags() {
            // タグ付きの学習記録を作成
            Tag tag = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Java")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            mockStudyRecord.setTags(List.of(tag));

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));

            // 実行
            StudyRecordDetailResponse response = studyRecordService.findById(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getTags()).containsExactly("Java");
        }

        @Test
        @DisplayName("正常系：タグがnullの場合でも正しく動作する")
        void findById_Success_WithoutTags() {
            mockStudyRecord.setTags(null);

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));

            // 実行
            StudyRecordDetailResponse response = studyRecordService.findById(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getTags()).isEmpty();
        }

        @Test
        @DisplayName("異常系：学習記録が存在しない場合、ResourceNotFoundExceptionがスローされる")
        void findById_NotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.findById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("学習記録が見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void findById_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.findById(1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("update メソッドのテスト")
    class UpdateTests {

        @Test
        @DisplayName("正常系：科目名のみを更新できる")
        void update_Success_SubjectOnly() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordUpdateResponse response = studyRecordService.update(1L, updateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(studyRecordRepository, times(1)).save(any(StudyRecord.class));
        }

        @Test
        @DisplayName("正常系：複数項目を同時に更新できる")
        void update_Success_MultipleFields() {
            // 複数項目を設定
            StudyRecordUpdateRequest multiUpdateRequest = new StudyRecordUpdateRequest();
            TestUtil.setField(multiUpdateRequest, "date", LocalDate.of(2024, 2, 1));
            TestUtil.setField(multiUpdateRequest, "subject", "Python");
            TestUtil.setField(multiUpdateRequest, "studyMinutes", 90);
            TestUtil.setField(multiUpdateRequest, "memo", "Pythonの基礎を学習");

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordUpdateResponse response = studyRecordService.update(1L, multiUpdateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(dateValidator, times(1)).validateYear(2024);
            verify(studyRecordRepository, times(1)).save(any(StudyRecord.class));
        }

        @Test
        @DisplayName("正常系：タグを更新できる（既存タグ）")
        void update_Success_WithExistingTags() {
            // タグを設定
            updateRequest.setTags(List.of("Python"));

            // 既存タグのモック
            Tag existingTag = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Python")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));
            when(tagRepository.findByUserIdAndNameIn(1L, List.of("Python")))
                    .thenReturn(List.of(existingTag));
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordUpdateResponse response = studyRecordService.update(1L, updateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(tagRepository, times(1)).findByUserIdAndNameIn(1L, List.of("Python"));
            verify(tagRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("正常系：タグを更新できる（新規タグ作成）")
        void update_Success_WithNewTags() {
            // タグを設定
            updateRequest.setTags(List.of("Python"));

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));
            when(tagRepository.findByUserIdAndNameIn(1L, List.of("Python"))).thenReturn(List.of());
            when(studyRecordRepository.save(any(StudyRecord.class))).thenReturn(mockStudyRecord);

            // 実行
            StudyRecordUpdateResponse response = studyRecordService.update(1L, updateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(tagRepository, times(1)).findByUserIdAndNameIn(1L, List.of("Python"));
            verify(tagRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("異常系：全項目がnullの場合、BadRequestExceptionがスローされる")
        void update_AllNull_ThrowsBadRequestException() {
            // 全項目nullのリクエストを作成
            StudyRecordUpdateRequest nullRequest = new StudyRecordUpdateRequest();

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.update(1L, nullRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("少なくとも1項目は更新項目として指定してください");

            // モックの呼び出し検証
            verify(currentUserProvider, never()).getUserId();
            verify(studyRecordRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("異常系：学習記録が存在しない場合、ResourceNotFoundExceptionがスローされる")
        void update_NotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.update(1L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("学習記録が見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(studyRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void update_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.update(1L, updateRequest))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("異常系：学習日が範囲外の場合、BadRequestExceptionがスローされる")
        void update_InvalidYear_ThrowsBadRequestException() {
            // 範囲外の年を設定
            StudyRecordUpdateRequest invalidRequest = new StudyRecordUpdateRequest();
            TestUtil.setField(invalidRequest, "date", LocalDate.of(1999, 1, 1));

            // モックの設定
            doThrow(new BadRequestException("yearは2000年〜2027年の範囲で指定してください"))
                    .when(dateValidator).validateYear(1999);

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.update(1L, invalidRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("yearは2000年〜2027年の範囲で指定してください");

            // モックの呼び出し検証
            verify(dateValidator, times(1)).validateYear(1999);
            verify(currentUserProvider, never()).getUserId();
            verify(studyRecordRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("delete メソッドのテスト")
    class DeleteTests {

        @Test
        @DisplayName("正常系：学習記録を削除できる")
        void delete_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.of(mockStudyRecord));
            doNothing().when(studyRecordRepository).delete(mockStudyRecord);

            // 実行
            StudyRecordDeleteResponse response = studyRecordService.delete(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("deleted");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(studyRecordRepository, times(1)).delete(mockStudyRecord);
        }

        @Test
        @DisplayName("異常系：学習記録が存在しない場合、ResourceNotFoundExceptionがスローされる")
        void delete_NotFound_ThrowsResourceNotFoundException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(studyRecordRepository.findByIdAndUserId(1L, 1L))
                    .thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.delete(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("学習記録が見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(studyRecordRepository, never()).delete(mockStudyRecord);
        }

        @Test
        @DisplayName("異常系：認証情報が不正な場合、UnauthorizedExceptionがスローされる")
        void delete_Unauthorized_ThrowsUnauthorizedException() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenThrow(new UnauthorizedException("認証が必要です"));

            // 実行・検証
            assertThatThrownBy(() -> studyRecordService.delete(1L))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("認証が必要です");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(studyRecordRepository, never()).findByIdAndUserId(anyLong(), anyLong());
            verify(studyRecordRepository, never()).delete(mockStudyRecord);
        }
    }
}
