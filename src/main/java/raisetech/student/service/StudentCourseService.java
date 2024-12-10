package raisetech.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.Repository.StudentRepository;
import raisetech.student.data.StudentCourse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生情報とコース情報を管理するサービスクラス
 */
@Service
public class StudentCourseService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentCourseService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private boolean filterByCourseName(StudentCourse course, String courseName) {
        return courseName.equalsIgnoreCase(course.getCourseName());
    }

    /**
     * 指定されたコース名に基づいて学生リストを検索します。
     *
     * @param courseName コース名
     * @return 指定されたコース名の学生リスト
     */
    public List<StudentCourse> searchStudentListByCourseName(String courseName) {
        List<StudentCourse> courses = studentRepository.findAllStudentCourses();
        return courses.stream()
                .filter(course -> filterByCourseName(course, courseName))
                .collect(Collectors.toList());
    }

    /**
     * すべての学生コース情報を取得します。
     *
     * @return 学生コース情報のリスト
     */
    public List<StudentCourse> getAllstudentCourseService() {
        return studentRepository.findAllStudentCourses();
    }
}