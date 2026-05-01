package com.example.studytracker.controller;

import com.example.studytracker.dto.export.ExportCsvRequest;
import com.example.studytracker.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * エクスポート関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@Tag(name = "エクスポートAPI", description = "学習記録のCSVエクスポートなどのエクスポート関連API")
public class ExportController {

    private final ExportService exportService;

    /**
     * 指定期間の学習記録をCSV形式でエクスポートする
     *
     * GET /export/csv?from={from}&to={to}
     *
     * @param request CSVエクスポートリクエスト（from/to必須）
     * @return CSVデータ
     */
    @GetMapping("/csv")
    @Operation(
            summary = "CSVエクスポート",
            description = "指定期間の学習記録をCSV形式でエクスポートする。fromとtoは必須パラメータ。")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "エクスポート成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "パラメータ不正（必須パラメータ欠落、日付フォーマット違反など）"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> exportCsv(
            @Parameter(description = "CSVエクスポートリクエスト", required = true)
            @Valid @ModelAttribute ExportCsvRequest request) {

        log.debug("[{}] exportCsv request: from={}, to={}", this.getClass().getSimpleName(), request.getFrom(), request.getTo());

        // CSVデータを生成
        String csvData = exportService.exportToCsv(request);

        // レスポンスを返却（text/csv）
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}
