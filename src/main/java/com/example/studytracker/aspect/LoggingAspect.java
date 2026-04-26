package com.example.studytracker.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * ログ出力用AOPアスペクトクラス
 * Controller層とService層のメソッド開始・終了・例外を自動ログ出力する
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * ControllerとServiceの全メソッドを対象にログ出力を行う
     * メソッド開始・終了・例外発生時のログを出力する
     *
     * @param joinPoint 実行中のメソッド情報
     * @return メソッドの戻り値
     * @throws Throwable メソッド実行時に発生した例外
     */
    @Around("execution(* com.example.studytracker.controller..*(..)) || " +
            "execution(* com.example.studytracker.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[{}] {} start", className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] {} end", className, methodName);
            return result;
        } catch (Throwable ex) {
            // スタックトレースはExceptionHandlerで出力するため、ここではメッセージのみ出力
            log.error("[{}] {} error: {}", className, methodName, ex.getMessage());
            throw ex;
        }
    }
}
