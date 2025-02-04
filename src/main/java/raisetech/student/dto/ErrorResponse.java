package raisetech.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

// Lombokを利用したコンストラクタ生成
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private HttpStatus httpStatus;

    // 引数3つのカスタムコンストラクタ
    public ErrorResponse(String message, String details, int statusCode) {
        this.message = message;
        this.httpStatus = HttpStatus.valueOf(statusCode); // 数値コードを HttpStatus に変換
    }
}
