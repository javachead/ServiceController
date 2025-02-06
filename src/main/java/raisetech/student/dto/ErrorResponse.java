package raisetech.student.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// Lombokを利用したコンストラクタ生成
@Getter
public class ErrorResponse {
    private String message;
    private HttpStatus httpStatus;

    // 引数3つのカスタムコンストラクタ
    public ErrorResponse(String message, String details, int statusCode) {
        this.message = message;
        this.httpStatus = HttpStatus.valueOf(statusCode); // 数値コードを HttpStatus に変換
    }
}
