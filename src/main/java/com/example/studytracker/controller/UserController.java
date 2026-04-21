package com.example.studytracker.controller;

import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.dto.user.UserResponse;
import com.example.studytracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー関連のAPIエンドポイントを提供するControllerクラス
 *
 * 設計書「APIリクエスト・レスポンス.md」3.1 自分の情報取得 に基づく実装
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "ユーザーAPI", description = "ユーザー情報取得などのユーザー関連API")
public class UserController {

    private final UserService userService;

    /**
     * 自分の情報を取得する
     * <p>
     * GET /users/me
     *
     * @return ユーザーレスポンス
     */
    @GetMapping("/me")
    @Operation(summary = "自分の情報取得", description = "ログイン中のユーザーの情報を取得する")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "取得成功"
            ),
            @ApiResponse(
                    responseCode = "401", description = "未認証"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(SuccessResponse.success(userResponse));
    }
}
