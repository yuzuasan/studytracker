package com.example.studytracker.service;

import com.example.studytracker.dto.tag.TagCreateRequest;
import com.example.studytracker.dto.tag.TagCreateResponse;
import com.example.studytracker.dto.tag.TagDeleteResponse;
import com.example.studytracker.dto.tag.TagListResponse;
import com.example.studytracker.entity.StudyRecord;
import com.example.studytracker.entity.Tag;
import com.example.studytracker.entity.User;
import com.example.studytracker.exception.ConflictException;
import com.example.studytracker.exception.ResourceNotFoundException;
import com.example.studytracker.repository.TagRepository;
import com.example.studytracker.repository.UserRepository;
import com.example.studytracker.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TagServiceの単体テスト
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TagService 単体テスト")
class TagServiceTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private TagCreateRequest tagCreateRequest;
    private User mockUser;
    private Tag mockTag;
    private StudyRecord mockStudyRecord;

    @BeforeEach
    void setUp() {
        // テスト用データのセットアップ
        tagCreateRequest = new TagCreateRequest();
        tagCreateRequest.setName("Java");

        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("hashedPassword")
                .githubUsername("githubuser")
                .build();

        mockTag = Tag.builder()
                .id(1L)
                .user(mockUser)
                .name("Java")
                .build();

        mockStudyRecord = StudyRecord.builder()
                .id(1L)
                .user(mockUser)
                .tags(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("create メソッドのテスト")
    class CreateTests {

        @Test
        @DisplayName("正常系：タグ作成が成功する")
        void create_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByUserIdAndName(1L, "Java")).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(tagRepository.save(any(Tag.class))).thenReturn(mockTag);

            // 実行
            TagCreateResponse response = tagService.create(tagCreateRequest);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByUserIdAndName(1L, "Java");
            verify(userRepository, times(1)).findById(1L);
            verify(tagRepository, times(1)).save(any(Tag.class));
        }

        @Test
        @DisplayName("異常系：同名タグが既に存在する場合、ConflictExceptionがスローされる")
        void create_DuplicateTagName_ThrowsConflictException() {
            // モックの設定：同名タグが既に存在
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByUserIdAndName(1L, "Java")).thenReturn(Optional.of(mockTag));

            // 実行・検証
            assertThatThrownBy(() -> tagService.create(tagCreateRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("同じ名前のタグが既に存在します");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByUserIdAndName(1L, "Java");
            verify(userRepository, never()).findById(any());
            verify(tagRepository, never()).save(any(Tag.class));
        }

        @Test
        @DisplayName("異常系：ユーザーが存在しない場合、ResourceNotFoundExceptionがスローされる")
        void create_UserNotFound_ThrowsResourceNotFoundException() {
            // モックの設定：ユーザーが存在しない
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByUserIdAndName(1L, "Java")).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> tagService.create(tagCreateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("ユーザーが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByUserIdAndName(1L, "Java");
            verify(userRepository, times(1)).findById(1L);
            verify(tagRepository, never()).save(any(Tag.class));
        }
    }

    @Nested
    @DisplayName("getAll メソッドのテスト")
    class GetAllTests {

        @Test
        @DisplayName("正常系：タグ一覧が正しく取得できる")
        void getAll_Success() {
            // テスト用タグリストの作成
            Tag tag1 = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Java")
                    .build();
            Tag tag2 = Tag.builder()
                    .id(2L)
                    .user(mockUser)
                    .name("Spring")
                    .build();
            List<Tag> tags = List.of(tag1, tag2);

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(tags);

            // 実行
            TagListResponse response = tagService.getAll();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getTags()).hasSize(2);
            assertThat(response.getTags().get(0).getId()).isEqualTo(1L);
            assertThat(response.getTags().get(0).getName()).isEqualTo("Java");
            assertThat(response.getTags().get(1).getId()).isEqualTo(2L);
            assertThat(response.getTags().get(1).getName()).isEqualTo("Spring");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByUserIdOrderByNameAsc(1L);
        }

        @Test
        @DisplayName("正常系：タグが0件の場合でも正しく動作する")
        void getAll_EmptyList_Success() {
            // モックの設定：タグが0件
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByUserIdOrderByNameAsc(1L)).thenReturn(List.of());

            // 実行
            TagListResponse response = tagService.getAll();

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getTags()).isEmpty();

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByUserIdOrderByNameAsc(1L);
        }
    }

    @Nested
    @DisplayName("delete メソッドのテスト")
    class DeleteTests {

        @Test
        @DisplayName("正常系：タグ削除が成功する")
        void delete_Success() {
            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockTag));
            doNothing().when(tagRepository).delete(mockTag);

            // 実行
            TagDeleteResponse response = tagService.delete(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("deleted");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(tagRepository, times(1)).delete(mockTag);
        }

        @Test
        @DisplayName("正常系：タグに関連する学習記録がある場合、関連が解除されて削除される")
        void delete_WithStudyRecords_Success() {
            // タグに学習記録を関連付ける（Builderで作成）
            List<StudyRecord> studyRecords = new ArrayList<>();
            studyRecords.add(mockStudyRecord);
            Tag tagWithRecords = Tag.builder()
                    .id(1L)
                    .user(mockUser)
                    .name("Java")
                    .studyRecords(studyRecords)
                    .build();

            // 学習記録にタグを関連付ける
            mockStudyRecord.getTags().add(tagWithRecords);

            // モックの設定
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(tagWithRecords));
            doNothing().when(tagRepository).delete(tagWithRecords);

            // 実行
            TagDeleteResponse response = tagService.delete(1L);

            // 検証
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("deleted");

            // 学習記録からタグが削除されていることを確認
            assertThat(mockStudyRecord.getTags()).doesNotContain(tagWithRecords);

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(tagRepository, times(1)).delete(tagWithRecords);
        }

        @Test
        @DisplayName("異常系：タグが存在しない場合、ResourceNotFoundExceptionがスローされる")
        void delete_TagNotFound_ThrowsResourceNotFoundException() {
            // モックの設定：タグが存在しない
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(tagRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

            // 実行・検証
            assertThatThrownBy(() -> tagService.delete(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("タグが見つかりません");

            // モックの呼び出し検証
            verify(currentUserProvider, times(1)).getUserId();
            verify(tagRepository, times(1)).findByIdAndUserId(1L, 1L);
            verify(tagRepository, never()).delete(any(Tag.class));
        }
    }
}
