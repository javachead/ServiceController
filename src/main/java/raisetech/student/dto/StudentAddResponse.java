package raisetech.student.dto;

import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import java.util.List;

/**
 * 学生登録用のレスポンスDTO
 */
public record StudentAddResponse(String message, Student student, List<StudentCourse> studentCourses) {}