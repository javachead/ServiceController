package raisetech.student.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 学生管理APIのRESTコントローラー。
 * 学生データおよび関連するコースデータの作成、取得、更新、削除を行うエンドポイントを提供します。
 * 主な機能:
 * 学生の登録・更新・削除
 * 学生に関連付けられたコースデータの登録・取得
 * 全学生データの取得
 * このコントローラーはトランザクション管理を取り入れており、
 * 適切なロールバックを通じてデータ整合性を保証します。
 */
@Validated
@RestController
@RequestMapping("/api/students")
public class StudentController {

    // 学生コースに関するサービスクラス
    private final StudentCourseService studentCourseService;

    // 学生詳細情報に関するサービスクラス
    private final StudentDetailService studentDetailService;

    // 学生情報に関するサービスクラス
    private final StudentService studentService;

    /**
     * コンストラクターインジェクションを通じてサービスを注入。
     *
     * @param studentCourseService 学生コース管理サービス
     * @param studentDetailService 学生詳細情報管理サービス
     * @param studentService       学生管理サービス
     */

    public StudentController(StudentCourseService studentCourseService,
                             StudentDetailService studentDetailService,
                             StudentService studentService) {
        this.studentCourseService = studentCourseService;
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
    }

    /**
     * 新しい学生情報を登録し、関連するコース情報も保存します。
     * <p>
     * このメソッドはトランザクション管理下で動作し、以下の操作を実行します：
     * 1. `Student`オブジェクトをデータベースに保存します。
     * 2. 学生に紐づく`StudentCourse`リストをデータベースに保存します。
     * 3. 正常に保存できた場合は201 Createdのレスポンスを返します。
     * 保存中にエラーが発生した場合は、500 Internal Server Errorのレスポンスを返します。
     *
     * @param student 新しい学生情報を含むリクエストボディ（JSON形式）。
     *                このオブジェクトは{@code @Valid}でバリデーションされます。
     * @return 保存に成功した場合、作成された学生情報を含むレスポンス（201 Created）。
     * 失敗した場合、エラーメッセージを含むレスポンス（500 Internal Server Error）。
     */

    @Transactional
    @PostMapping
    public ResponseEntity<StudentAddResponse> createStudent(@RequestBody @Valid Student student) {
        try {
            // 1. 学生情報を保存
            Student save = studentService.save(student);

            // 2. 学生に紐づくコース情報を保存
            List<StudentCourse> courseList = student.getStudentCourses(); // コース情報を取得
            if (courseList != null && !courseList.isEmpty()) {
                Long studentId = student.getId(); // StudentオブジェクトからIDを取得
                courseList.forEach(course -> course.setStudentId(studentId)); // 各コースにstudentIdを設定
                studentCourseService.courseList(courseList, studentId); // studentIdを渡す
            }

            // 3. 登録成功レスポンスを返却
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StudentAddResponse("学生情報および関連コースが正常に保存されました", save));
        } catch (Exception e) {
            // サーバーエラー時のハンドリング
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StudentAddResponse("エラー: 学生および関連コースの登録に失敗しました", student));
        }
    }

    /**
     * 指定されたIDの学生情報を取得するエンドポイント。
     *
     * @param id 学生ID (1以上の値である必要あり)
     * @return 成功時に学生情報を返す
     * @throws StudentNotFoundException 学生IDが存在しない場合にスローされる
     * @throws RuntimeException         その他のサーバーエラー発生時にスローされる
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable @Min(1) Long id) {
        try {
            // 学生情報の取得
            Student student = studentService.getStudentById(id);
            return ResponseEntity.ok(new StudentResponse("指定されたIDの学生を取得しました", student.getId()));
        } catch (StudentNotFoundException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("サーバーエラーが発生しました", e);
        }
    }

    /**
     * 指定されたIDの学生情報を更新するエンドポイント。
     *
     * @param id            更新対象の学生ID
     * @param studentDetail 更新する学生の詳細情報（学生情報およびコース情報を含む）
     * @return 更新成功時にHTTPステータス200 (OK) と学籍情報を返す
     * @throws StudentNotFoundException 学生IDが存在しない場合
     */
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid StudentDetail studentDetail) {
        try {
            // 学生データの更新処理
            Student updatedStudent = studentDetail.getStudent();
            updatedStudent.setId(id);

            // 学生情報を保存または更新
            studentService.save(updatedStudent);

            // コース情報を一括保存・更新
            List<StudentCourse> updatedCourses = studentDetail.getStudentCourses();
            if (updatedCourses != null && !updatedCourses.isEmpty()) {
                studentCourseService.courseList(updatedCourses, id); // `courseList` を呼び出し
            }

            // 正常なレスポンスを返却
            return ResponseEntity.ok(new StudentResponse("学生データが正常に更新されました", updatedStudent.getId()));
        } catch (StudentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StudentResponse("指定された学生IDは存在しません", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StudentResponse("学生更新処理に失敗しました", null));
        }
    }

    /**
     * 全学生情報を取得するエンドポイント。
     *
     * @return 学生のリストを含むレスポンス
     * @throws Exception サーバー内でエラーが発生した場合
     */
    @GetMapping
    public ResponseEntity<StudentsResponse> getAllStudents() {
        try {
            // 学生一覧を取得
            Optional<List<StudentDetail>> optionalStudents =
                    Optional.ofNullable(studentDetailService.findAllStudentDetails());
            List<StudentDetail> students = optionalStudents.orElse(Collections.emptyList());

            // データが存在しない場合のレスポンス
            if (students.isEmpty()) {
                return ResponseEntity.ok(new StudentsResponse("学生データが存在しません", students));
            }
            return ResponseEntity.ok(new StudentsResponse("学生一覧を取得しました", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 学生情報を削除するエンドポイント。
     *
     * @param id 削除対象の学生ID (1以上の値である必要あり)
     * @return 削除成功時にメッセージを返す
     * @throws StudentNotFoundException 学生IDが存在しない場合
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StudentDeleteResponse> removeStudent(@PathVariable @Min(1) Long id) {
        try {
            studentService.deleteStudentById(id);
            // クライアントに論理削除の具体的な説明を避ける
            return ResponseEntity.ok(new StudentDeleteResponse("学生が削除されました"));
        } catch (StudentNotFoundException ex) {
            // 存在しない場合は404を返却
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StudentDeleteResponse("指定された学生IDが存在しません"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StudentDeleteResponse("削除処理に失敗しました"));
        }
    }
}
