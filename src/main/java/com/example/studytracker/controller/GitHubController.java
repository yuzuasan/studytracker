package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.github.GitHubCommitsRequest;
import com.example.studytracker.dto.github.GitHubCommitsResponse;
import com.example.studytracker.service.GitHubService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GitHub連携関連のAPIエンドポイントを提供するControllerクラス
 */
@Slf4j
@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
@Tag(name = "GitHub連携API", description = "GitHub連携関連API")
public class GitHubController {

    private final GitHubService gitHubService;

    /**
     * 指定期間のGitHubコミット数を取得する
     *
     * GET /github/commits
     *
     * @param request コミット数取得リクエスト（クエリパラメータ）
     * @return コミット数取得レスポンス
     */
    @GetMapping("/commits")
    @Operation(summary = "コミット数取得", description = "指定期間のGitHubコミット数を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "400", description = "パラメータ不正"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            ),
            @ApiResponse(
                    responseCode = "500", description = "GitHub APIエラー"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<GitHubCommitsResponse>> getCommitCount(
            @Parameter(description = "コミット数取得リクエスト")
            @ModelAttribute GitHubCommitsRequest request) {
        log.debug("[{}] getCommitCount request: from={}, to={}",
                this.getClass().getSimpleName(), request.getFrom(), request.getTo());
        GitHubCommitsResponse response = gitHubService.getCommitCount(request);
        return ResponseEntity.ok()
                .body(SuccessResponse.success(response));
    }
}
