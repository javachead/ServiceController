package raisetech.student.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import raisetech.student.domain.StudentDetail;

import java.util.List;

/**
 * すべての学生情報取得用のレスポンスDTO
 */
@Schema(description = "すべての学生情報取得用レスポンスDTO")
public record StudentsResponse(
        @Schema(description = "処理結果のメッセージ", example = "すべての学生情報の取得に成功しました") String message,

        @ArraySchema(
                schema = @Schema(description = "学生詳細情報のリスト"),
                arraySchema = @Schema(description = "学生詳細情報を格納した配列リスト")
        )
        List<StudentDetail> data
) {
}
