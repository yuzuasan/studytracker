package com.example.studytracker.controller;

import com.example.studytracker.dto.auth.LoginRequest;
import com.example.studytracker.dto.auth.LoginResponse;
import com.example.studytracker.dto.auth.RegisterRequest;
import com.example.studytracker.dto.auth.RegisterResponse;
import com.example.studytracker.dto.common.SuccessResponse;
import com.example.studytracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証関連のController
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "認証API", description = "ユーザー登録・ログインなどの認証関連API")
public class AuthController {

    private final AuthService authService;

    /**
     * ユーザー登録
     * 
     * @param request 登録リクエスト
     * @return 登録レスポンス
     */
    @PostMapping("/register")
    @Operation(summary = "ユーザー登録", description = "新しいユーザーを登録する")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "登録成功"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "入力不正（バリデーションエラー）"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "username重複"
        )
    })
    public ResponseEntity<SuccessResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.success(response));
    }

    /**
     * ログイン
     *
     * @param request ログインリクエスト
     * @return ログインレスポンス（JWTトークン）
     */
    @PostMapping("/login")
    @Operation(summary = "ログイン", description = "ユーザー認証を行い、JWTトークンを発行する")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "ログイン成功"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "入力不正（バリデーションエラー）"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "認証失敗（ユーザー名またはパスワードが不正）"
        )
    })
    public ResponseEntity<SuccessResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(SuccessResponse.success(response));
    }
}
