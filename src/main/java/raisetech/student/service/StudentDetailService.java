package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentDetailService {

    private final StudentRepository studentRepository; // リポジトリ使用
    private final StudentCourseService studentCourseService; // コースサービス使用

    // コンストラクタによる依存性注入
    public StudentDetailService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 全学生情報とそのコース情報を統合して取得 (学生詳細情報リストを生成)
     */
    public List<StudentDetail> findAllStudentDetails() {
        // 学生の基本情報リストを取得
        List<Student> students = studentRepository.findAllStudents();
        List<StudentDetail> studentDetails = new ArrayList<>();

        for (Student student : students) {
            // 学生情報を保持する StudentDetail オブジェクトを作成
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            // 学生に紐づくコース情報を取得し、設定
            List<StudentCourse> courses = studentCourseService.findByStudentId(Long.valueOf(student.getId()));
            studentDetail.setStudentCourses(courses);

            // 学生の詳細情報をリストに追加
            studentDetails.add(studentDetail);
        }

        return studentDetails; // 全学生の詳細情報を返す
    }

    /**
     * 特定の学生情報を更新 (学生の基本データと紐付くコース情報の更新)
     *
     * @param student 更新対象の学生オブジェクト
     * @return 更新後の学生詳細情報リスト
     */
    public List<StudentDetail> updateStudentDetails(Student student) {
        // 学生情報をリポジトリ経由で更新
        studentRepository.updateStudentDetails(student);

        // 紐づくコース情報を取得または更新
        List<StudentCourse> updatedCourses = studentCourseService.updateByStudentId(Math.toIntExact(student.getId())); // 修正済み

        // 学生詳細情報を構築
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student); // 学生情報を設定
        studentDetail.setStudentCourses(updatedCourses); // 更新済みのコース情報を設定

        // 学生詳細情報をリスト化して返す
        List<StudentDetail> studentDetails = new ArrayList<>();
        studentDetails.add(studentDetail);

        return studentDetails; // 更新後の詳細情報を返す
    }
}