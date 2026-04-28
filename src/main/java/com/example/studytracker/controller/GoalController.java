package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.goal.GoalCreateRequest;
import com.example.studytracker.dto.goal.GoalCreateResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
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
}
