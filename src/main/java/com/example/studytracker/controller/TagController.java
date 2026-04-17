package com.example.studytracker.controller;

import com.example.studytracker.dto.common.ApiResponse;
import com.example.studytracker.dto.tag.TagCreateRequest;
import com.example.studytracker.dto.tag.TagCreateResponse;
import com.example.studytracker.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * タグ管理関連のAPIエンドポイントを提供するControllerクラス
 */
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "作成成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "入力不正（バリデーションエラー）"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "同名タグが既に存在"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<TagCreateResponse>> create(
            @Valid @RequestBody TagCreateRequest request) {
        TagCreateResponse response = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
