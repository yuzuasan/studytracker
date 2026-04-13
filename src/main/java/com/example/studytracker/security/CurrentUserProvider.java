package com.example.studytracker.security;

import com.example.studytracker.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 認証情報取得共通クラス
 * SecurityContextからuserIdを取得する
 *
 * 利用方法:
 * Long userId = currentUserProvider.getUserId();
 */
@Component
public class CurrentUserProvider {

    /**
     * 現在ログイン中のユーザーIDを取得する
     *
     * @return ユーザーID
     * @throws UnauthorizedException 未認証の場合
     */
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 認証情報が存在しない、または未認証の場合
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("認証が必要です");
        }

        // principalからuserIdを取得
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("認証情報が不正です");
        }

        // StringのuserIdをLongに変換
        try {
            return Long.valueOf(principal.toString());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("認証情報が不正です");
        }
    }
}
