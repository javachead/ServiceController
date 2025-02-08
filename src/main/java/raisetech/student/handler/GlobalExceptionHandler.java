package raisetech.student.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
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
     *
     * @param ex MethodArgumentNotValidException インスタンス
     * @return 400 BAD_REQUEST とフィールドごとのエラーメッセージ
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("【入力値バリデーションエラー】: {}", ex.getMessage());

        // 入力値エラーのすべてのフィールドとメッセージを収集
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> {
            String errorMessage = switch (error.getField()) {
                case "name" -> "名前は必須です";
                case "kanaName" -> "名前（カナ）は全角カタカナのみ使用できます";
                case "email" -> "メールアドレスの形式が正しくありません";
                case "area" -> "住所（エリア）は必須です";
                case "age" -> "年齢は0歳以上150歳以下である必要があります";
                case "sex" -> "性別は「男性」「女性」「その他」のいずれかを指定してください";
                case "courseName" -> "コース名は必須です";
                default -> error.getDefaultMessage();
            };
            errors.put(error.getField(), errorMessage);
        });
        // エラーレスポンスを作成
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * ConstraintViolationExceptionのハンドリング。
     * （例えば、@Minや@Maxアノテーション違反時）
     *
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
     * JSONパースエラー（InvalidFormatException）のハンドリング.
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException ex) {
        log.warn("【JSONフォーマットエラー】: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "リクエストJSONの形式が正しくありません。",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * リクエストメソッドが許可されていない場合（HttpRequestMethodNotSupportedException）のハンドリング。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("【HTTPメソッドエラー】: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "サポートされていないHTTPメソッドです。",
                ex.getMessage(),
                HttpStatus.METHOD_NOT_ALLOWED.value()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * 必須パラメータが不足している場合（MissingServletRequestParameterException）のハンドリング。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn("【必須パラメータエラー】: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "必須パラメータが不足しています。",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 最大アップロードサイズ超過時のハンドリング（MaxUploadSizeExceededException）。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("【アップロードサイズ制限エラー】: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "アップロード可能なファイルサイズを超えています。",
                ex.getMessage(),
                HttpStatus.PAYLOAD_TOO_LARGE.value()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    /**
     * 存在しないページへのリクエスト（NoHandlerFoundException）のハンドリング。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("【ページ未検出エラー】: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "指定されたURLのページが見つかりません。",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 学生が見つからない場合（StudentNotFoundException）のハンドリング。
     *
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
     *
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
