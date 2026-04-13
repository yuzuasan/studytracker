package com.example.studytracker.security;

import com.example.studytracker.dto.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT認証フィルタ
 * AuthorizationヘッダからJWTを検証し、SecurityContextに認証情報をセットする
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * Authorizationヘッダーのプレフィックス
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * フィルタ処理
     * 1. Authorizationヘッダを取得
     * 2. JWTを検証
     * 3. userIdを取得
     * 4. SecurityContextにセット
     *
     * @param request  HTTPリクエスト
     * @param response HTTPレスポンス
     * @param chain    フィルタチェーン
     * @throws ServletException サーブレット例外
     * @throws IOException      IO例外
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Authorizationヘッダを取得
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // ヘッダが存在しない、またはBearer形式でない場合は次のフィルタへ
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // Bearerプレフィックスを除去してトークンを取得
        String token = authHeader.substring(BEARER_PREFIX.length());

        // トークンが空の場合は次のフィルタへ
        if (token.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // JWT検証
        if (!jwtUtil.validateToken(token)) {
            log.warn("JWTトークンの検証に失敗しました: {}", request.getRequestURI());

            // ErrorResponseを使用してレスポンスを生成
            ErrorResponse errorResponse = ErrorResponse.error("認証に失敗しました");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        // userIdを抽出
        String userId = jwtUtil.extractUserId(token);

        // SecurityContextに認証情報をセット
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("認証成功: userId={}, uri={}", userId, request.getRequestURI());

        chain.doFilter(request, response);
    }
}
