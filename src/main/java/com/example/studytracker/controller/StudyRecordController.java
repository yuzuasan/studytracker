package com.example.studytracker.controller;

import com.example.studytracker.dto.common.ApiResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateRequest;
import com.example.studytracker.dto.studyrecord.StudyRecordCreateResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordDetailResponse;
import com.example.studytracker.dto.studyrecord.StudyRecordListResponse;
import com.example.studytracker.service.StudyRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "登録成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "入力不正（バリデーションエラー）"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<StudyRecordCreateResponse>> create(
            @Valid @RequestBody StudyRecordCreateRequest request) {
        StudyRecordCreateResponse response = studyRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * 学習記録一覧を取得する
     *
     * GET /study-records
     *
     * @return 学習記録一覧レスポンス
     */
    @GetMapping
    @Operation(summary = "学習記録一覧取得", description = "ログインユーザーの学習記録一覧を取得する")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<StudyRecordListResponse>> findAll() {
        StudyRecordListResponse response = studyRecordService.findAll();
        return ResponseEntity.ok(ApiResponse.success(response));
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", description = "データなし"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<StudyRecordDetailResponse>> findById(
            @PathVariable Long id) {
        StudyRecordDetailResponse response = studyRecordService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
