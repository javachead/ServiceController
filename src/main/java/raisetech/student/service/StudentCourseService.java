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

    /**
     * 学生IDに基づくコース情報を取得
     */
    public List<StudentCourse> findByStudentId(Long studentId) {
        return studentCourseRepository.findCoursesByStudentId(studentId);
    }

    /**
     * 学生のコース情報を保存
     */
    public void saveCourses(List<StudentCourse> courses, Long studentId) {
        for (StudentCourse course : courses) {
            course.setStudentId(studentId); // 学生IDをセット
            studentCourseRepository.insertCourse(course); // リポジトリに挿入
        }
    }
}