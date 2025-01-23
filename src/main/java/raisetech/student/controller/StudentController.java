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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    // ロガー(MDCなどを活用するとさらに詳細ログ出力が可能ですが、シンプルな例を維持)
    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    // DI（依存性注入）されたサービス
    private final StudentCourseService studentCourseService;
    private final StudentDetailService studentDetailService;
    private final StudentService studentService;

    // コンストラクタでDI（依存性注入）
    public StudentController(StudentCourseService studentCourseService,
                             StudentDetailService studentDetailService,
                             StudentService studentService) {
        this.studentCourseService = studentCourseService;
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
    }

    // ---------------- 学生登録 ----------------
    /**
     * 新しい学生とそのコース情報を登録するAPI
     * 入力のバリデーションは @Valid アノテーションで行われる
     * @param studentDetail 学生情報（学生そのものとコース情報も含める）
     * @return StudentAddResponse: 登録成功時の結果詳細
     */
    @PostMapping
    public ResponseEntity<StudentAddResponse> createStudent(@RequestBody @Valid StudentDetail studentDetail) {
        logger.info("学生登録リクエストを受け付けました: {}", studentDetail);
        try {
            // Student と StudentCourseを分離して保存
            Student student = studentDetail.getStudent();
            studentService.saveStudent(student); // 学生情報を保存
            logger.info("学生データを保存しました: {}", student);

            // 学生コース情報の保存 (関連するすべてのコースを学生IDと紐付けて保存)
            List<StudentCourse> studentCourses = studentDetail.getStudentCourses();
            studentCourses.forEach(course -> {
                course.setStudentId(student.getId()); // 保存時に学生IDが必要になる
                studentCourseService.saveStudentCourse(course);
                logger.info("学生コースデータを保存しました: {}", course);
            });

            // レスポンスを生成
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StudentAddResponse(
                            "新しい学生とコースが登録されました",
                            student,
                            studentCourses
                    ));
        } catch (Exception e) {
            // 登録処理中のエラー記録
            logger.error("学生登録中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------------- 学生取得 ----------------

    /**
     * 特定のIDに紐づく学生情報を取得するAPI
     * @param id 学生ID
     * @return StudentResponse: 指定されたIDの学生情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        logger.info("学生取得リクエストを受け付けました。ID: {}", id);
        try {
            // Optional を利用してデータを取得
            Optional<Student> optionalStudent = Optional.ofNullable(studentService.getStudentById(id));

            // データが存在しない場合、レスポンスを返す
            if (optionalStudent.isEmpty()) {
                logger.warn("指定されたIDの学生が見つかりません。ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StudentResponse("指定された学生が見つかりません", null));
            }

            // 存在する場合は値を取得してレスポンスを生成
            Student student = optionalStudent.get();
            logger.info("指定されたIDの学生を取得しました: {}", student);
            return ResponseEntity.ok(new StudentResponse("指定されたIDの学生を取得しました", student));
        } catch (Exception e) {
            // エラー発生時のログ出力と500エラーのレスポンス
            logger.error("学生取得中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ---------------- 学生一覧取得 ----------------
    /**
     * すべての学生情報の一覧を取得するAPI
     * @return StudentsResponse: 登録されているすべての学生情報
     */
    @GetMapping
    public ResponseEntity<StudentsResponse> getAllStudents() {
        logger.info("学生一覧取得リクエストを受け付けました");
        try {
            // Optional を利用して全学生情報を取得
            Optional<List<StudentDetail>> optionalStudents = Optional.ofNullable(studentDetailService.findAllStudentDetails());

            // Optional 内のリストが空である場合、レスポンスを生成
            List<StudentDetail> students = optionalStudents.orElse(Collections.emptyList());
            if (students.isEmpty()) {
                logger.warn("学生データが存在しません");
                return ResponseEntity.ok(new StudentsResponse("学生データが存在しません", Collections.emptyList()));
            }

            logger.info("学生一覧を取得しました。件数: {}", students.size());
            return ResponseEntity.ok(new StudentsResponse("学生一覧を取得しました", students));
        } catch (Exception e) {
            // 学生一覧取得中のエラー記録
            logger.error("学生一覧取得中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------------- 学生削除 ----------------
    /**
     * 指定された学生を削除するAPI
     * @param id 学生ID
     * @return StudentDeleteResponse: 削除結果のメッセージ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StudentDeleteResponse> removeStudent(@PathVariable Long id) {
        logger.info("学生削除リクエストを受け付けました。ID: {}", id);
        try {
            // Optional を利用して削除対象を取得
            Optional<Student> optionalStudent = Optional.ofNullable(studentService.getStudentById(id));

            // 存在しない場合のエラーハンドリング
            if (optionalStudent.isEmpty()) {
                logger.warn("指定された学生が見つかりません。ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StudentDeleteResponse("指定された学生が見つかりません"));
            }

            // 存在する場合は削除処理を実行
            studentService.deleteStudentById(id);
            logger.info("指定された学生を削除しました。ID: {}", id);

            return ResponseEntity.ok(new StudentDeleteResponse("学生が削除されました"));
        } catch (Exception e) {
            // エラー発生時のログ出力と500エラーのレスポンス
            logger.error("学生削除中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ======== コントローラー内部DTO ========
    /** 内部で完結するため、別ファイル化の対応なし判断 */
    private record StudentAddResponse(String message, Student student, List<StudentCourse> studentCourses) {}
    private record StudentResponse(String message, Student student) {}
    private record StudentsResponse(String message, List<StudentDetail> data) {}
    private record StudentDeleteResponse(String message) {}
}