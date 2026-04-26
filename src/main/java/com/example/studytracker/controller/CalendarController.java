package com.example.studytracker.controller;

import com.example.studytracker.dto.calendar.CalendarResponse;
import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学習カレンダー関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Tag(name = "カレンダーAPI", description = "学習カレンダーの取得などのカレンダー関連API")
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * 学習カレンダーを取得する
     *
     * GET /calendar?year={year}&month={month}
     *
     * @param year  年（任意、未指定時は現在年）
     * @param month 月（任意、未指定時は現在月）
     * @return 日別学習時間のリスト
     */
    @GetMapping
    @Operation(
            summary = "学習カレンダー取得",
            description = "指定された年月の学習カレンダーを取得する。year/month未指定時は現在年月を使用。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "パラメータ不正"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<CalendarResponse>>> getCalendar(
            @Parameter(description = "年（YYYY形式）", example = "2026")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "月（1〜12）", example = "4")
            @RequestParam(required = false) Integer month) {

        log.debug("[{}] getCalendar request: year={}, month={}", this.getClass().getSimpleName(), year, month);
        List<CalendarResponse> calendar = calendarService.getCalendar(year, month);

        return ResponseEntity.ok(SuccessResponse.success(calendar));
    }
}
