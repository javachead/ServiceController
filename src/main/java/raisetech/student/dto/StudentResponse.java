package raisetech.student.dto;

import raisetech.student.data.Student;

/**
 * 特定の学生情報取得用のレスポンスDTO
 */
public record StudentResponse(String message, Student student) {}