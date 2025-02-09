package raisetech.student.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * エラーレスポンスDTOクラス
 * エラー発生時にクライアントへ返却する詳細情報を含む構造。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private int status; // ステータスコード (例: 400, 404, 500)
    private String message; // 表示用エラーメッセージ
    private String details; // 詳細情報
    private String timestamp; // エラーメッセージのタイムスタンプ

    public ErrorResponse(String status, String message, int details) {
        this.status = Integer.parseInt(status);
        this.message = message;
        this.details = String.valueOf(details);
    }
}
