package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.util.List;

@Service
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    public List<StudentCourse> getCoursesByStudentId(Long studentId) {
        // 特定の学生に紐づく全コースを取得
        return studentCourseRepository.findCoursesByStudentId(Math.toIntExact(studentId));
    }

    // (今後、日付ロジックを適用する場合に備えたメソッド)
    public boolean isActiveCourse(StudentCourse course) {
        return true; // 現時点では無条件で true を返す
    }
}