package raisetech.student.service;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourses;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentCoursesMapper;
import raisetech.student.repository.StudentMapper;

import java.util.List;

@Service
@Transactional
public class StudentDetailService {

    private final StudentMapper studentMapper;
    private final StudentCoursesMapper studentCoursesMapper;

    // コンストラクタでリポジトリ注入
    public StudentDetailService(StudentMapper studentMapper, StudentCoursesMapper studentCoursesMapper) {
        this.studentMapper = studentMapper;
        this.studentCoursesMapper = studentCoursesMapper;
    }

    // 全学生の詳細情報を取得する
    public List<StudentDetail> findAllStudentDetails() {
        // 学生情報を全件取得
        List<Student> students = studentMapper.findAll();

        // 各学生に関連するコースをマッピング
        return students.stream()
                .map(student -> {
                    List<StudentCourses> studentCourses = studentCoursesMapper.findByStudentId(student.getId());
                    return new StudentDetail(student, studentCourses); // 学生とそのコース情報を結合
                })
                .collect(Collectors.toList());
    }

    public void saveStudentDetail(StudentDetail studentDetail) {
        // 学生データの登録
        Student student = studentDetail.getStudent();
        studentMapper.insertStudent(student);

        if (studentDetail.getStudentCourses() != null) {
            for (StudentCourses course : studentDetail.getStudentCourses()) {
                // 関連するコースに学生IDを紐付け
                course.setStudentId(student.getId());
                studentCoursesMapper.insert(course);
            }
        }
    }
}