package raisetech.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import raisetech.student.data.Student;

/**
 * 学生登録用のレスポンスDTO
 */
@Schema(description = "学生を登録した際のレスポンスDTO")
public record StudentAddResponse(
        @Schema(description = "処理結果メッセージ", example = "学生の登録が成功しました") String message,
        @Schema(description = "登録済みの学生情報") Student student) {
}
