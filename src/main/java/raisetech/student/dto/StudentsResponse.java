package raisetech.student.dto;

import raisetech.student.domain.StudentDetail;

import java.util.List;

/**
 * すべての学生情報取得用のレスポンスDTO
 */
public record StudentsResponse(String message, List<StudentDetail> data) {
}
