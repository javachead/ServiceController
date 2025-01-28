package raisetech.student.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentRepository;

@Service
public class StudentDetailService {

    private final StudentRepository studentRepository; // 学生関連のデータアクセス (リポジトリ)
    private final StudentCourseService studentCourseService; // 学生コース関連のサービス

    // コンストラクタによる依存性注入
    public StudentDetailService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 全学生情報とそのコース情報を統合して取得するメソッド。
     * 学生と紐付くコース情報を取得し、学生詳細情報 ({@link StudentDetail}) のリストを生成します。
     * 小規模プロジェクトを想定しており、メモリ不足エラーは考慮していませんが、
     * 学生数が増加した場合にはスケーラブルなデータ処理（例えばページング処理）への対応が必要です。
     * @return 全学生の詳細情報（学生情報および紐付くコース情報）のリスト
     */
    public List<StudentDetail> findAllStudentDetails() {
        return studentRepository.findAllStudents().stream()
                .map(student -> {
                    // 学生ごとに関連するコースを取得
                    List<StudentCourse> courses = studentCourseService.findByStudentId(student.getId());
                    return buildStudentDetail(student, courses); // buildStudentDetail を呼び出す
                })
                .collect(Collectors.toList());
    }

    /**
     * 学生の紐付くコース情報を個別に更新
     * @param student 学生オブジェクト
     * @return 更新後の学生コース情報リスト
     */
    private List<StudentCourse> updateStudentCourses(Student student) {
        try {
            return studentCourseService.updateByStudentId(Math.toIntExact(student.getId()));
        } catch (Exception e) {
            throw new RuntimeException("紐付くコース情報の更新中にエラーが発生しました: " + student.getId(), e);
        }
    }

    /**
     * 学生情報とコース情報を基に詳細情報を生成
     * @param student 学生オブジェクト
     * @param courses 更新済みコース情報リスト
     * @return 学生詳細情報
     */
    private StudentDetail buildStudentDetail(Student student, List<StudentCourse> courses) {
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student); // 学生情報の設定
        studentDetail.setStudentCourses(courses); // 紐付くコース情報の設定
        return studentDetail;
    }
}