package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.tag.TagCreateRequest;
import com.example.studytracker.dto.tag.TagCreateResponse;
import com.example.studytracker.dto.tag.TagDeleteResponse;
import com.example.studytracker.dto.tag.TagListResponse;
import com.example.studytracker.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * タグ管理関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "タグ管理API", description = "タグの作成・取得・削除などのタグ管理関連API")
public class TagController {

    private final TagService tagService;

    /**
     * タグを作成する
     *
     * POST /tags
     *
     * @param request タグ作成リクエスト
     * @return タグ作成レスポンス
     */
    @PostMapping
    @Operation(summary = "タグ作成", description = "新しいタグを作成する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "作成成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "入力不正（バリデーションエラー）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "409", description = "同名タグが既に存在"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<TagCreateResponse>> create(
            @Valid @RequestBody TagCreateRequest request) {
        log.debug("[{}] create request: name={}", this.getClass().getSimpleName(), request.getName());
        TagCreateResponse response = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response));
    }

    /**
     * タグ一覧を取得する
     *
     * GET /tags
     *
     * @return タグ一覧レスポンス
     */
    @GetMapping
    @Operation(summary = "タグ一覧取得", description = "ログイン中のユーザーのタグ一覧を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<TagListResponse>> getAll() {
        log.debug("[{}] getAll request", this.getClass().getSimpleName());
        TagListResponse response = tagService.getAll();
        return ResponseEntity.ok(SuccessResponse.success(response));
    }

    /**
     * タグを削除する
     *
     * DELETE /tags/{id}
     *
     * @param id タグID（パスパラメータ）
     * @return タグ削除レスポンス
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "タグ削除", description = "指定されたタグを削除する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "削除成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "404", description = "タグが存在しない"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<TagDeleteResponse>> delete(@PathVariable Long id) {
        log.debug("[{}] delete request: id={}", this.getClass().getSimpleName(), id);
        TagDeleteResponse response = tagService.delete(id);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }
}
