package raisetech.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * エラーレスポンスDTOクラス
 * クライアントにエラー時の詳細情報を返却するデータ構造
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status;        // HTTPステータスコード (例: 400, 404, 500)
    private String message;    // 表示用のエラーメッセージ
    private String details;    // エラーの詳細情報（例外の詳細など）
    private String timestamp;  // 発生時刻を表すタイムスタンプ（ISO形式）

    /**
     * コンストラクタ: タイムスタンプを自動生成してエラーレスポンスを作成します。
     *
     * @param status  エラーのHTTPステータスコード
     * @param message 表示用エラーメッセージ
     * @param details 問題の詳細
     */
    public ErrorResponse(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timestamp = generateTimestamp();
    }

    /**
     * 現在の日付・時刻を ISO 8601 形式で生成
     *
     * @return タイムスタンプ文字列
     */
    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
