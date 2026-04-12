package com.example.studytracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

/**
 * JWT生成・検証ユーティリティクラス
 * HS256署名アルゴリズムを使用
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * シークレットキーからHMAC-SHA256キーを生成する
     * 短いキーの場合はSHA-256ハッシュで256ビットに拡張する
     *
     * @return SecretKey HMAC-SHA256キー
     */
    private SecretKey getSigningKey() {
        try {
            // シークレットキーをSHA-256でハッシュ化（256ビットに拡張）
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(
                    jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
            );
            return new SecretKeySpec(keyBytes, "HmacSHA256");
        } catch (Exception e) {
            throw new RuntimeException("JWT署名キーの生成に失敗しました", e);
        }
    }

    /**
     * JWTトークンを生成する
     * ペイロードにはsub（ユーザー識別子）、exp（有効期限）、iat（発行時刻）を含める
     *
     * @param userId ユーザーID
     * @return JWTトークン
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * JWTトークンからユーザーIDを抽出する
     *
     * @param token JWTトークン
     * @return ユーザーID
     */
    public String extractUserId(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * JWTトークンから有効期限を抽出する
     *
     * @param token JWTトークン
     * @return 有効期限
     */
    public Date extractExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * JWTトークンを検証する
     * 署名検証と有効期限チェックを行う
     *
     * @param token JWTトークン
     * @return 検証結果（true: 有効、false: 無効）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWTトークンが期限切れです");
        } catch (SignatureException e) {
            log.warn("JWT署名が無効です");
        } catch (MalformedJwtException e) {
            log.warn("JWTトークンの形式が不正です");
        } catch (UnsupportedJwtException e) {
            log.warn("サポートされていないJWTトークンです");
        } catch (IllegalArgumentException e) {
            log.warn("JWTトークンが空です");
        }
        return false;
    }

    /**
     * JWTトークンを解析してクレームを取得する
     *
     * @param token JWTトークン
     * @return クレーム情報
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
