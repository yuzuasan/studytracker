package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.stat.DailyStatsRequest;
import com.example.studytracker.dto.stat.DailyStatsResponse;
import com.example.studytracker.dto.stat.MonthlyStatsResponse;
import com.example.studytracker.dto.stat.SubjectStatsResponse;
import com.example.studytracker.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        log.debug("[{}] getDailyStats request: from={}, to={}", this.getClass().getSimpleName(), request.getFrom(), request.getTo());
        List<DailyStatsResponse> dailyStats = statisticsService.getDailyStats(request);

        return ResponseEntity.ok(SuccessResponse.success(dailyStats));
    }

    /**
     * 月別統計を取得する
     *
     * GET /stats/monthly
     *
     * @return 月別統計のリスト
     */
    @GetMapping("/monthly")
    @Operation(
            summary = "月別統計取得",
            description = "月単位で学習時間を集計して取得する。年月昇順で返却される。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<MonthlyStatsResponse>>> getMonthlyStats() {

        log.debug("[{}] getMonthlyStats request", this.getClass().getSimpleName());
        List<MonthlyStatsResponse> monthlyStats = statisticsService.getMonthlyStats();

        return ResponseEntity.ok(SuccessResponse.success(monthlyStats));
    }

    /**
     * 科目別統計を取得する
     *
     * GET /stats/subjects
     *
     * @return 科目別統計のリスト
     */
    @GetMapping("/subjects")
    @Operation(
            summary = "科目別統計取得",
            description = "科目ごとの学習時間を集計して取得する。学習時間降順で返却される。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<SubjectStatsResponse>>> getSubjectStats() {

        log.debug("[{}] getSubjectStats request", this.getClass().getSimpleName());
        List<SubjectStatsResponse> subjectStats = statisticsService.getSubjectStats();

        return ResponseEntity.ok(SuccessResponse.success(subjectStats));
    }
}
