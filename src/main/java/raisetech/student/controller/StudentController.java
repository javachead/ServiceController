package raisetech.student.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.dto.StudentAddResponse;
import raisetech.student.dto.StudentDeleteResponse;
import raisetech.student.dto.StudentResponse;
import raisetech.student.dto.StudentsResponse;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// 学生情報を管理するコントローラークラスです。
// 基本的なCRUD操作（登録・取得・削除）を提供します。
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
            // 学生情報を登録
            Student student = studentDetail.getStudent();
            studentService.saveStudent(student);
            logger.info("学生データを保存しました: {}", student);

            // コース情報を登録
            List<StudentCourse> studentCourses = studentDetail.getStudentCourses();
            studentCourses.forEach(course -> {
                course.setStudentId(student.getId()); // 学生IDをコースに紐付ける
                studentCourseService.saveCourse(course);
                logger.info("学生コースデータを保存しました: {}", course);
            });

            // 登録成功レスポンスを返却
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StudentAddResponse(
                            "新しい学生とコースが登録されました",
                            student,
                            studentCourses
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
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        logger.info("学生取得リクエストを受け付けました。ID: {}", id);
        try {
            // 学生情報の取得を試みる
            Optional<Student> optionalStudent = Optional.ofNullable(studentService.getStudentById(id));
            if (optionalStudent.isEmpty()) {
                // 学生が見つからない場合の処理
                logger.warn("指定されたIDの学生が見つかりません。ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StudentResponse("指定された学生が見つかりません", null));
            }

            // 学生データが見つかった場合の処理
            Student student = optionalStudent.get();
            logger.info("指定されたIDの学生を取得しました: {}", student);
            return ResponseEntity.ok(new StudentResponse("指定されたIDの学生を取得しました", student));
        } catch (Exception e) {
            // エラー発生時のログ出力とレスポンス
            logger.error("学生取得中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<StudentDeleteResponse> removeStudent(@PathVariable Long id) {
        logger.info("学生削除リクエストを受け付けました。ID: {}", id);
        try {
            // 指定されたIDの学生データが存在するか確認
            Optional<Student> optionalStudent = Optional.ofNullable(studentService.getStudentById(id));
            if (optionalStudent.isEmpty()) {
                // 学生が見つからない場合のレスポンス
                logger.warn("指定された学生が見つかりません。ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StudentDeleteResponse("指定された学生が見つかりません"));
            }

            // 学生データの削除処理
            studentService.deleteStudentById(id);
            logger.info("指定された学生を削除しました。ID: {}", id);
            return ResponseEntity.ok(new StudentDeleteResponse("学生が削除されました"));
        } catch (Exception e) {
            // エラー発生時のログ出力とレスポンス
            logger.error("学生削除中にエラーが発生しました。ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}