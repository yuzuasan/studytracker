package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.goal.GoalCreateRequest;
import com.example.studytracker.dto.goal.GoalCreateResponse;
import com.example.studytracker.dto.goal.GoalDeleteResponse;
import com.example.studytracker.dto.goal.GoalListResponse;
import com.example.studytracker.dto.goal.GoalUpdateRequest;
import com.example.studytracker.dto.goal.GoalUpdateResponse;
import com.example.studytracker.service.GoalService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 目標管理関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Tag(name = "目標管理API", description = "目標の設定・取得・更新・削除などの目標管理関連API")
public class GoalController {

    private final GoalService goalService;

    /**
     * 目標を作成する
     *
     * POST /goals
     *
     * @param request 目標作成リクエスト
     * @return 目標作成レスポンス
     */
    @PostMapping
    @Operation(summary = "目標設定", description = "新しい目標を設定する")
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
                    responseCode = "409", description = "同一月の目標が既に存在"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<GoalCreateResponse>> create(
            @Valid @RequestBody GoalCreateRequest request) {
        log.debug("[{}] create request: month={}, targetMinutes={}",
                this.getClass().getSimpleName(), request.getMonth(), request.getTargetMinutes());
        GoalCreateResponse response = goalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.success(response));
    }

    /**
     * 目標一覧を取得する
     *
     * GET /goals
     *
     * @return 目標一覧レスポンス
     */
    @GetMapping
    @Operation(summary = "目標取得", description = "目標一覧と達成状況を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<GoalListResponse>> getAllGoals() {
        log.debug("[{}] getAllGoals request", this.getClass().getSimpleName());
        GoalListResponse response = goalService.getAllGoals();
        return ResponseEntity.ok()
                .body(SuccessResponse.success(response));
    }

    /**
     * 目標を更新する
     *
     * PUT /goals/{id}
     *
     * @param id 目標ID
     * @param request 目標更新リクエスト
     * @return 目標更新レスポンス
     */
    @PutMapping("/{id}")
    @Operation(summary = "目標更新", description = "指定された目標の学習時間を更新する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "更新成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "入力不正（バリデーションエラー）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "404", description = "データなし"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<GoalUpdateResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody GoalUpdateRequest request) {
        log.debug("[{}] update request: id={}, targetMinutes={}",
                this.getClass().getSimpleName(), id, request.getTargetMinutes());
        GoalUpdateResponse response = goalService.update(id, request);
        return ResponseEntity.ok()
                .body(SuccessResponse.success(response));
    }

    /**
     * 目標を削除する
     *
     * DELETE /goals/{id}
     *
     * @param id 目標ID
     * @return 目標削除レスポンス
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "目標削除", description = "指定された目標を削除する")
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
    public ResponseEntity<SuccessResponse<GoalDeleteResponse>> delete(
            @PathVariable Long id) {
        log.debug("[{}] delete request: id={}",
                this.getClass().getSimpleName(), id);
        GoalDeleteResponse response = goalService.delete(id);
        return ResponseEntity.ok()
                .body(SuccessResponse.success(response));
    }
}
