package com.example.studytracker.exception;

import com.example.studytracker.dto.common.ErrorResponse;
import com.example.studytracker.dto.common.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * グローバル例外ハンドラー
 * アプリケーション全体の例外を統一的にハンドリングする
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * カスタム例外：ResourceNotFoundException（404）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * カスタム例外：UnauthorizedException（401）
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponse errorResponse = ErrorResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * カスタム例外：BadRequestException（400）
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = ErrorResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * カスタム例外：ConflictException（409）
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        ErrorResponse errorResponse = ErrorResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * バリデーションエラー（400）
     * @Validアノテーションによるバリデーション失敗時に呼ばれる
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorResponse.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationErrorResponse.ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ValidationErrorResponse errorResponse = ValidationErrorResponse.validationError(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * JSONパースエラー（400）
     * 日付フォーマット不正など、リクエストボディのパース失敗時に呼ばれる
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {

        // 日付パースエラーの場合は専用メッセージ
        String message = "Request body parse error";
        if (ex.getMessage() != null && ex.getMessage().contains("LocalDate")) {
            message = "dateはYYYY-MM-DD形式で入力してください";
        }

        ErrorResponse errorResponse = ErrorResponse.error(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * その他の未ハンドリング例外（500）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.error("Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
