package raisetech.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学生削除用のレスポンスDTO
 */
@Schema(description = "学生削除操作のレスポンスDTO")
public record StudentDeleteResponse(
        @Schema(description = "削除処理の結果メッセージ", example = "学生が正常に削除されました") String message
) {
}
