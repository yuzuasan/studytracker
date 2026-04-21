package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDeleteResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDetailResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordListResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordSearchCondition;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordUpdateResponse;
import com.example.studytracker.service.StudyRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学習記録関連のAPIエンドポイントを提供するControllerクラス
 */
@RestController
@RequestMapping("/study-records")
@RequiredArgsConstructor
@Tag(name = "学習記録API", description = "学習記録の登録・取得・更新・削除などの学習記録関連API")
public class StudyRecordController {

    private final StudyRecordService studyRecordService;

    /**
     * 学習記録を登録する
     *
     * POST /study-records
     *
     * @param request 学習記録登録リクエスト
     * @return 学習記録登録レスポンス
     */
    @PostMapping
    @Operation(summary = "学習記録登録", description = "新しい学習記録を登録する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "登録成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "入力不正（バリデーションエラー）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StudyRecordCreateResponse>> create(
            @Valid @RequestBody StudyRecordCreateRequest request) {
        StudyRecordCreateResponse response = studyRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response));
    }

    /**
     * 学習記録一覧を取得する（検索条件付き）
     *
     * GET /study-records
     * GET /study-records?from=2026-04-01&to=2026-04-30
     * GET /study-records?tag=spring
     * GET /study-records?keyword=Boot
     *
     * @param condition 検索条件（クエリパラメータ、任意）
     * @return 学習記録一覧レスポンス
     */
    @GetMapping
    @Operation(summary = "学習記録一覧取得", description = "ログインユーザーの学習記録一覧を取得する。検索条件（from/to/tag/keyword）で絞り込み可能。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StudyRecordListResponse>> findAll(
            @ModelAttribute StudyRecordSearchCondition condition) {
        StudyRecordListResponse response = studyRecordService.findAll(condition);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }

    /**
     * 学習記録詳細を取得する
     *
     * GET /study-records/{id}
     *
     * @param id 学習記録ID
     * @return 学習記録詳細レスポンス
     */
    @GetMapping("/{id}")
    @Operation(summary = "学習記録詳細取得", description = "指定されたIDの学習記録詳細を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "404", description = "データなし"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StudyRecordDetailResponse>> findById(
            @PathVariable Long id) {
        StudyRecordDetailResponse response = studyRecordService.findById(id);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }

    /**
     * 学習記録を部分更新する
     *
     * PATCH /study-records/{id}
     *
     * @param id 学習記録ID
     * @param request 学習記録更新リクエスト
     * @return 学習記録更新レスポンス
     */
    @PatchMapping("/{id}")
    @Operation(summary = "学習記録更新", description = "指定されたIDの学習記録を部分更新する。更新したい項目のみ指定してください。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "更新成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "入力不正（空リクエスト含む）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "404", description = "データなし"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StudyRecordUpdateResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudyRecordUpdateRequest request) {
        StudyRecordUpdateResponse response = studyRecordService.update(id, request);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }

    /**
     * 学習記録を削除する
     *
     * DELETE /study-records/{id}
     *
     * @param id 学習記録ID
     * @return 学習記録削除レスポンス
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "学習記録削除", description = "指定されたIDの学習記録を削除する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "削除成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "404", description = "データなし"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StudyRecordDeleteResponse>> delete(
            @PathVariable Long id) {
        StudyRecordDeleteResponse response = studyRecordService.delete(id);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }
}
