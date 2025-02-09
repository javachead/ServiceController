package raisetech.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 特定の学生情報取得用のレスポンスDTO
 */
@Schema(description = "特定の学生情報取得用のレスポンスDTO")
public record StudentResponse(
        @Schema(description = "処理結果のメッセージ", example = "学生情報の取得が成功しました") String message,
        @Schema(description = "学生のID", example = "12345") Long student
) {
}
