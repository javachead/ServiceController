package raisetech.student.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.dto.StudentAddResponse;
import raisetech.student.dto.StudentDeleteResponse;
import raisetech.student.dto.StudentResponse;
import raisetech.student.dto.StudentsResponse;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// 学生情報を管理するコントローラークラスです。
// 基本的なCRUD操作（登録・取得・削除）を提供します。
@Validated
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    // 学生コースに関するサービスクラス
    private final StudentCourseService studentCourseService;

    // 学生詳細情報に関するサービスクラス
    private final StudentDetailService studentDetailService;

    // 学生情報に関するサービスクラス
    private final StudentService studentService;

    // コンストラクタでサービスクラスを注入
    public StudentController(StudentCourseService studentCourseService,
                             StudentDetailService studentDetailService,
                             StudentService studentService) {
        this.studentCourseService = studentCourseService;
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
    }

    /**
     * 新しい学生情報を登録します。
     * 学生データおよび関連するコース情報を保存しています。
     * @param studentDetail 学生情報およびコース情報を含むリクエストボディ
     * @return 登録完了時に作成された学生とコース情報を含むレスポンス
     */
    @PostMapping
    public ResponseEntity<StudentAddResponse> createStudent(@RequestBody @Valid StudentDetail studentDetail) {
        logger.info("学生登録リクエストを受け付けました: {}", studentDetail);

        try {
            // 学生情報と関連するコース情報を登録
            studentDetailService.saveStudentAndCourses(studentDetail);
            logger.info("学生データおよびコースデータを保存しました: {}", studentDetail);

            // 登録成功レスポンスを返却
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StudentAddResponse(
                            "新しい学生とコースが登録されました",
                            studentDetail.getStudent(),
                            studentDetail.getStudentCourses()
                    ));
        } catch (Exception e) {
            // エラー発生時のログ出力とレスポンス
            logger.error("学生登録中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 指定されたIDの学生情報を取得します。
     * @param id 学生ID
     * @return 指定された学生データを含むレスポンス
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable @Min(1) Long id) {
        logger.info("学生取得リクエストを受け付けました。ID: {}", id);
        try {
            // 学生情報の取得
            Student student = studentService.getStudentById(id);

            // 学生データが見つかった場合のレスポンス
            logger.info("指定されたIDの学生を取得しました: {}", student);
            return ResponseEntity.ok(new StudentResponse("指定されたIDの学生を取得しました", student.getId()));
        } catch (StudentNotFoundException ex) {
            // 学生が見つからない場合の処理はCustom Exceptionへ委譲
            logger.warn("指定されたIDの学生が見つかりません。ID: {}", id, ex);
            throw ex; // ExceptionはGlobalExceptionHandlerへ委譲
        } catch (Exception e) {
            // その他のエラー発生時の処理
            logger.error("学生取得中にエラーが発生しました。ID: {}", id, e);
            throw new RuntimeException("サーバーエラーが発生しました", e);
        }
    }

    /**
     * 指定されたIDの学生データを更新します。
     * @param id 更新対象の学生のID（URLパス変数）
     * @param studentDetail 更新する学生情報（リクエストボディ）
     * @return 更新結果のレスポンス
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid StudentDetail studentDetail) {

        logger.info("学生更新リクエストを受け付けました。ID: {}, データ: {}", id, studentDetail);

        try {
            // 学生データの更新処理
            Student updatedStudent = studentDetail.getStudent();
            updatedStudent.setId(id);
            studentService.updateStudent(updatedStudent);

            // コース情報更新処理
            List<StudentCourse> updatedCourses = studentDetail.getStudentCourses();
            studentCourseService.saveCourses(updatedCourses, id); // saveCoursesを利用
            logger.info("すべてのコースデータを更新または新規登録しました: 学生ID={}, 件数={}", id, updatedCourses.size());

            // 正常レスポンスを返却
            return ResponseEntity.ok(new StudentResponse("学生データが更新されました", updatedStudent.getId()));

        } catch (StudentNotFoundException ex) {
            // 学生が見つからない場合、例外スロー
            logger.warn("指定されたIDの学生が見つかりませんでした。ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StudentResponse("指定された学生IDは存在しません", null));
        } catch (Exception e) {
            // その他のエラー処理
            logger.error("学生更新処理中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StudentResponse("学生更新処理に失敗しました", null));
        }
    }

    /**
     * 全ての学生情報を取得します。
     * @return 学生データの一覧を含むレスポンス
     */
    @GetMapping
    public ResponseEntity<StudentsResponse> getAllStudents() {
        logger.info("学生一覧取得リクエストを受け付けました");
        try {
            // 学生一覧を取得
            Optional<List<StudentDetail>> optionalStudents = Optional.ofNullable(studentDetailService.findAllStudentDetails());
            List<StudentDetail> students = optionalStudents.orElse(Collections.emptyList());

            // データが存在しない場合のレスポンス
            if (students.isEmpty()) {
                logger.warn("学生データが存在しません");
                return ResponseEntity.ok(new StudentsResponse("学生データが存在しません", Collections.emptyList()));
            }

            // 正常データのレスポンス
            logger.info("学生一覧を取得しました。件数: {}", students.size());
            return ResponseEntity.ok(new StudentsResponse("学生一覧を取得しました", students));
        } catch (Exception e) {
            // エラー発生時のログ出力とレスポンス
            logger.error("学生一覧取得中にエラーが発生しました", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 指定されたIDの学生データを削除します。
     * @param id 削除対象の学生ID
     * @return 削除成功または失敗のレスポンス
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StudentDeleteResponse> removeStudent(@PathVariable @Min(1) Long id) {
        logger.info("学生削除リクエストを受け付けました。ID: {}", id);
        try {
            // 削除処理の呼び出し
            studentService.deleteStudentById(id);
            logger.info("指定された学生を削除しました。ID: {}", id);

            // クライアントに論理削除の具体的な説明を避ける
            return ResponseEntity.ok(new StudentDeleteResponse("学生が削除されました"));
        } catch (StudentNotFoundException ex) {
            // 存在しない場合は404を返却
            logger.warn("指定された学生が見つかりませんでした。ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StudentDeleteResponse("指定された学生IDが存在しません"));
        } catch (Exception e) {
            // その他の予期しない例外
            logger.error("削除処理中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StudentDeleteResponse("削除処理に失敗しました"));
        }
    }
}
