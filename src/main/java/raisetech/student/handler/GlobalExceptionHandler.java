package raisetech.student.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import raisetech.student.dto.ErrorResponse;
import raisetech.student.exception.StudentNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション全体で例外を統一的に処理する例外ハンドラー。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 入力値バリデーションエラー（MethodArgumentNotValidException）のハンドリング。
     * @param ex MethodArgumentNotValidException インスタンス
     * @return 400 BAD_REQUEST とフィールドごとのエラーメッセージ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("【入力値バリデーションエラー】: {}", ex.getMessage());

        // 入力値エラーのすべてのフィールドとメッセージを収集
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // エラーレスポンスを作成
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * ConstraintViolationExceptionのハンドリング。
     * （例えば、@Minや@Maxアノテーション違反時）
     * @param ex ConstraintViolationException インスタンス
     * @return 400 BAD_REQUEST とエラーレスポンス
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("【入力データ制約違反】: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                "入力データに問題があります",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 学生が見つからない場合（StudentNotFoundException）のハンドリング。
     * @param ex StudentNotFoundException
     * @return HTTP 404 NOT_FOUND とエラーレスポンス
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn("【StudentNotFoundException】: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                "指定された学生IDが見つかりませんでした。",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * その他の予期しない例外のハンドリング。
     * @param ex Exception インスタンス
     * @return 500 INTERNAL_SERVER_ERROR とエラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("【予期しないエラー発生】: {}", ex.getMessage(), ex);

        ErrorResponse response = new ErrorResponse(
                "サーバーエラーが発生しました。サポートに連絡してください。",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}