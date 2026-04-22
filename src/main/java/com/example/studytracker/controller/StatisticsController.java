package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.stat.DailyStatsRequest;
import com.example.studytracker.dto.stat.DailyStatsResponse;
import com.example.studytracker.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学習統計関連のAPIエンドポイントを提供するControllerクラス
 */
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
@Tag(name = "統計API", description = "学習統計の取得などの統計関連API")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 日別統計を取得する
     *
     * GET /stats/daily?from={from}&to={to}
     *
     * @param request 日別統計リクエスト（from/to必須）
     * @return 日別統計のリスト
     */
    @GetMapping("/daily")
    @Operation(
            summary = "日別統計取得",
            description = "指定期間の日別学習時間を集計して取得する。fromとtoは必須パラメータ。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "パラメータ不正（必須パラメータ欠落、日付フォーマット違反など）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<DailyStatsResponse>>> getDailyStats(
            @Parameter(description = "日別統計リクエスト", required = true)
            @Valid @ModelAttribute DailyStatsRequest request) {

        List<DailyStatsResponse> dailyStats = statisticsService.getDailyStats(request);

        return ResponseEntity.ok(SuccessResponse.success(dailyStats));
    }
}
