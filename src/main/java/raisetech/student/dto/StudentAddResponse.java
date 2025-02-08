package raisetech.student.dto;

import raisetech.student.data.Student;

/**
 * 学生登録用のレスポンスDTO
 */
public record StudentAddResponse(String message, Student student) {
}
