package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.streak.StreakResponse;
import com.example.studytracker.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ストリーク関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/streak")
@RequiredArgsConstructor
@Tag(name = "ストリークAPI", description = "連続学習日数の取得などのストリーク関連API")
public class StreakController {

    private final StreakService streakService;

    /**
     * 現在の連続学習日数を取得する
     *
     * GET /streak
     *
     * @return 連続学習日数レスポンス
     */
    @GetMapping
    @Operation(summary = "連続学習日数取得", description = "現在の連続学習日数（ストリーク）を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<StreakResponse>> getCurrentStreak() {
        log.debug("[{}] getCurrentStreak request", this.getClass().getSimpleName());
        StreakResponse response = streakService.getCurrentStreak();
        return ResponseEntity.ok()
                .body(SuccessResponse.success(response));
    }
}
