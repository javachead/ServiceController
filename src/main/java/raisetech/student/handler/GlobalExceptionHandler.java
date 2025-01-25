package raisetech.student.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import raisetech.student.dto.ErrorResponse;
import raisetech.student.exception.StudentNotFoundException;

/**
 * アプリケーション全体で例外を統一的に処理する例外ハンドラー。
 * `@RestControllerAdvice` を使用して各コントローラ層で発生した例外をキャッチし、
 * HTTP ステータスコードに基づく適切なレスポンスを生成します。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラー（不正なリクエスト）をハンドリング。
     * @param ex IllegalArgumentException インスタンス
     * @return 400 BAD_REQUEST と適切なエラーメッセージ
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("【バリデーションエラー】: {}", ex.getMessage());

        // エラーレスポンスを作成
        ErrorResponse response = new ErrorResponse(
                "不正なリクエストです",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    /**
     * 学生が見つからない場合に対応する例外ハンドラー。
     * StudentNotFoundException がスローされた際に適切に処理を行い、
     * @param ex 学生が見つからない場合にスローされる StudentNotFoundException
     * @return HTTPステータスコード 404（NOT FOUND）と例外メッセージを含むレスポンスエンティティ
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException ex) {

        // エラーレスポンスを生成
        ErrorResponse response = new ErrorResponse(
                "学生が見つかりません",     // ユーザー向けのメッセージ
                ex.getMessage(),          // 例外の詳細なメッセージ（内部向け情報）
                HttpStatus.NOT_FOUND.value() // HTTPステータスコード
        );

        // HTTP 404 ステータスコードとエラーレスポンスを送信
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 全般的なアプリケーション例外をキャッチ。
     * @param ex Exception インスタンス
     * @return 500 INTERNAL_SERVER_ERROR とエラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("【予期しないエラー発生】: {}", ex.getMessage(), ex);

        // クライアントには限定的な情報を送信し、内部情報の漏洩を防ぐ
        ErrorResponse response = new ErrorResponse(
                "サーバーエラーが発生しました。サポートに連絡してください。",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}