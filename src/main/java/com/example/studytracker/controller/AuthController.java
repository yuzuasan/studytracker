package com.example.studytracker.controller;

import com.example.studytracker.dto.auth.RegisterRequest;
import com.example.studytracker.dto.auth.RegisterResponse;
import com.example.studytracker.dto.common.ApiResponse;
import com.example.studytracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "登録成功"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "入力不正（バリデーションエラー）"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "username重複"
        )
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
