package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDetailService {
    private final StudentService studentService;
    private final StudentCourseService studentCourseService;

    public StudentDetailService(StudentService studentService, StudentCourseService studentCourseService) {
        this.studentService = studentService;
        this.studentCourseService = studentCourseService;
    }

    public List<StudentDetail> getStudentDetails() {
        // 学生を全件取得（nullの場合は空のリストをデフォルトとして使用）
        List<Student> students = Optional.ofNullable(studentService.getAllStudents()).orElseGet(ArrayList::new);

        return students.stream()
                .map(student -> {
                    // 各学生のコース情報を取得
                    List<StudentCourse> studentCourses = studentCourseService.getCoursesByStudentId(student.getId());

                    // 学生詳細情報を作成
                    StudentDetail studentDetail = new StudentDetail();
                    studentDetail.setStudent(student);
                    studentDetail.addCourses(studentCourses); // コース情報をセット
                    return studentDetail;
                })
                .toList(); // 学生詳細情報のリストを返却
    }
}