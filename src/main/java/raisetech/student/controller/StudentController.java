package raisetech.student.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import java.util.List;

@RestController // 課題33　RESTコントローラーへ変更
@RequestMapping("/api/studentsList")
public class StudentController {

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentCourseService studentCourseService;
    private final StudentDetailService studentDetailService;
    private final StudentService studentService;

    // コンストラクタによる依存性注入
    public StudentController(StudentCourseService studentCourseService,
                             StudentDetailService studentDetailService,
                             StudentService studentService) {
        this.studentCourseService = studentCourseService;
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
    }

    // ---------------- 新規登録 ----------------

    @PostMapping // ベースパス (/api/studentsList) に相対
    public ResponseEntity<?> addStudentWithCourses(@RequestBody @Valid StudentDetail studentDetail) {
        // `Student`情報を登録
        Student student = studentDetail.getStudent();
        studentService.saveStudent(student); // 自動生成IDが`student`にセットされる

        // `StudentCourses`情報を登録
        List<StudentCourse> studentCourses = studentDetail.getStudentCourses();
        studentCourses.forEach(course -> {
            course.setStudentId(student.getId()); // `student_id`を設定
            studentCourseService.saveStudentCourse(course);
        });

        return ResponseEntity.ok("新しい学生とコースが登録されました");
    }

    // ---------------- 一覧取得 ----------------

    @GetMapping // GETリクエスト（/api/students）
    public ResponseEntity<?> getStudentList() {
        List<StudentDetail> studentDetails = studentDetailService.findAllStudentDetails();
        if (studentDetails == null || studentDetails.isEmpty()) {
            logger.warn("学生一覧が空です");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("データが存在しません");
        }

        logger.info("学生一覧を取得しました。データ件数: {}", studentDetails.size());
        return ResponseEntity.ok(studentDetails);
    }

    // ---------------- コース取得 ----------------

    @GetMapping("/{id}/getCourses")
    public ResponseEntity<?> getStudentCourses(@PathVariable Long id) {
        // 学生IDに紐づくコース情報を取得
        List<StudentCourse> courses = studentCourseService.findByStudentId(id);

        if (courses == null || courses.isEmpty()) {
            logger.warn("学生ID {} に紐づくコースが見つかりません", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("学生に紐づくコースが見つかりません");
        }

        logger.info("学生ID {} のコース情報を取得しました: 件数={}", id, courses.size());
        return ResponseEntity.ok(courses);
    }

    // ---------------- 詳細取得 ----------------

    @GetMapping("/getStudent{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);

        if (student == null) {
            logger.warn("指定されたIDの学生が見つかりません。ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("指定された学生データが見つかりません");
        }

        logger.info("指定されたIDの学生を取得しました。ID: {}", student.getId());
        return ResponseEntity.ok(student);
    }

    // ---------------- 学生情報更新 ----------------

    @PostMapping("/updateStudent")
    public ResponseEntity<Boolean> updateStudent(@RequestBody StudentDetail studentDetail) {

        try {
            Student student = studentDetail.getStudent();

            // deleted プロパティが未初期化の場合の安全策
            if (!student.isDeleted()) { // Lombokの `isDeleted()` を使用
                student.setDeleted(false);
            }

            // 学生情報の更新処理
            studentService.updateStudentDetails(student.getId(), student);

            // 更新成功時に true を返す
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            // 更新処理でエラー発生時に false を返す
            return ResponseEntity.ok(false);
        }
    }
    // ---------------- 学生削除 ----------------

    @DeleteMapping("/deleteStudent{id}") // DELETEリクエスト（/api/students/{id}）
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        logger.info("指定された学生を削除しました。ID: {}", id);

        return ResponseEntity.ok("学生情報が削除されました");
    }
}