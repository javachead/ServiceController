package raisetech.student.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import raisetech.student.domain.StudentDetail;
import raisetech.student.dto.ErrorResponse;
import raisetech.student.dto.StudentAddResponse;
import raisetech.student.dto.StudentDeleteResponse;
import raisetech.student.dto.StudentResponse;
import raisetech.student.dto.StudentsResponse;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import java.util.List;

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
@Tag(name = "学生管理API", description = "学生データおよび関連コースデータを管理するAPI")
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
     * 指定されたIDの学生情報を取得するエンドポイント。
     *
     * @param id 学生ID (1以上の値である必要あり)
     * @return 成功時に学生情報を返す
     * @throws StudentNotFoundException 学生IDが存在しない場合にスローされる
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "指定されたIDの学生情報を取得",
            description = """
                          指定された学生IDに基づき、学生情報を取得します。
                          - 入力: 学生ID (例: 1001)
                          - 成功時、学生の詳細情報を返却します。
                          - 未登録のIDを指定した場合は404エラーとなります。
                          """,
            tags = {"学生管理API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "学生情報の取得に成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "正常取得例",
                                    value = """
                                            {
                                                "message": "指定されたIDの学生を取得しました",
                                                "studentId": 1001
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "指定された学生IDが見つかりません。",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "未登録IDエラー例",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "学生データが見つかりませんでした。",
                                              "details": "学生が見つかりません: ID = 1000000",
                                              "timestamp": "2025-02-11T05:37:37"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "データ取得中にエラーが発生しました",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "サーバーエラー例",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "エラーが発生しました",
                                              "details": "ERROR",
                                              "timestamp": "2023-11-02 12:34:56"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<StudentResponse> getStudent(@PathVariable @Min(1) Long id) {
        try {
            // IDをもとに学生情報を取得。見つからない場合は例外をスロー
            Student student = studentService.getStudentById(id);

            // 成功時のレスポンス
            return ResponseEntity.ok(new StudentResponse("指定されたIDの学生を取得しました", student.getId()));
        } catch (StudentNotFoundException ex) {
            throw new StudentNotFoundException("学生が見つかりません: ID = " + id);
        } catch (Exception ex) {
            throw new RuntimeException("サーバー内部で予期しないエラーが発生しました。", ex);
        }
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
    @Operation(
            summary = "新しい学生を登録",
            description = """
                          学生情報をデータベースに登録し、関連するコース情報も同時に保存します。
                          - 入力データはJSON形式です。
                          - 成功時に登録された学生情報を返します。
                          """,
            tags = {"学生管理API"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "学生情報および関連コースが正常に保存されました",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentAddResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "登録成功例",
                                    value = """
                                            {   "id": 1,
                                                "name": "田中太郎",
                                                "kanaName": "タナカタロウ",
                                                "nickname": "taro123",
                                                "email": "tanaka.taro@example.com",
                                                "area": "東京都新宿区",
                                                "age": 30,
                                                "sex": "男性",
                                                "remark": "特記事項なし",
                                                "deleted": false,
                                                "studentCourses": [
                                                    {
                                                        "id": 1,
                                                        "studentId": 1,
                                                        "courseName": "Javaプログラミング入門",
                                                        "courseStartAt": "2023-01-10",
                                                        "courseEndAt": "2023-03-20"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @ApiResponse(
            responseCode = "500",
            description = "サーバー内部エラー",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            name = "サーバーエラー例",
                            value = """
                                    {
                                      "status": 500,
                                      "message": "エラーが発生しました",
                                      "details": "ERROR",
                                      "timestamp": "2023-11-02 12:34:56"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<StudentAddResponse> createStudent(@RequestBody @Valid Student student) {
        // 学生情報の登録フロー
        Student savedStudent = studentService.save(null, student); // IDをnullにしてDBで自動生成
        studentCourseService.saveCourses(savedStudent, student.getStudentCourses());

        // 成功時のレスポンス
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StudentAddResponse("学生情報および関連コースが正常に保存されました", savedStudent));
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
    @Operation(
            summary = "学生情報を更新",
            description = """
                          学生IDを指定し、学生情報および関連するコース情報を更新します。
                          - 指定されたIDに基づいて更新を行います。
                          - 学生データおよび関連するコースデータを一括で更新可能です。
                          """,
            tags = {"学生管理API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "更新成功例",
                                    value = """
                                            {
                                                "message": "学生データが正常に更新されました",
                                                "studentId": 1001
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "学生が存在しない",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "学生が存在しない例",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "学生データが見つかりませんでした。",
                                              "details": "学生が見つかりません: ID = 1000000",
                                              "timestamp": "2025-02-11T05:37:37"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "サーバー内部エラー",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "サーバーエラー例",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "エラーが発生しました",
                                              "details": "ERROR",
                                              "timestamp": "2023-11-02 12:34:56"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid StudentDetail studentDetail) {
        // studentServiceで更新ロジックを実行し、更新結果を取得
        Student updatedStudent = studentService.save(id, studentDetail.getStudent());

        // 関連するコースを更新
        studentCourseService.saveCourses(updatedStudent, studentDetail.getStudentCourses());

        // 成功レスポンスを返却
        return ResponseEntity.ok(new StudentResponse("学生データが正常に更新されました", updatedStudent.getId()));
    }

    /**
     * 全学生情報を取得するエンドポイント。
     *
     * @return 学生のリストを含むレスポンス
     * @throws Exception サーバー内でエラーが発生した場合
     */
    @GetMapping
    @Operation(
            summary = "全学生情報を取得",
            description = """
                          登録されているすべての学生情報を取得します。
                          - ※全件取得のため、ページング対象外です。
                          - データが存在しない場合は応答を返します。
                          """,
            tags = {"学生管理API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "取得成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentsResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "データ存在時",
                                    value = """
                                            {
                                                "message": "学生一覧を取得しました",
                                                "students": [
                                                    {
                                                        "name": "田中太郎",
                                                        "kanaName": "タナカタロウ",
                                                        "nickname": "taro123",
                                                        "email": "tanaka.taro@example.com",
                                                        "area": "東京都新宿区",
                                                        "age": 30,
                                                        "sex": "男性",
                                                        "remark": "Javaに興味があります",
                                                        "deleted": false,
                                                        "studentCourses": [
                                                            {
                                                                "id": 1,
                                                                "studentId": 1001,
                                                                "courseName": "Javaプログラミング入門",
                                                                "courseStartAt": "2023-01-10",
                                                                "courseEndAt": "2023-03-20",
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "name": "山田花子",
                                                        "kanaName": "ヤマダハナコ",
                                                        "nickname": "hana456",
                                                        "email": "yamada.hana@example.com",
                                                        "area": "大阪府大阪市",
                                                        "age": 25,
                                                        "sex": "女性",
                                                        "remark": "フロントエンド開発を学んでいます",
                                                        "deleted": false,
                                                        "studentCourses": [
                                                            {
                                                                "id":2,
                                                                "studentId": 1002,
                                                                "courseName": "JavaScript",
                                                                "courseStartAt": "2023-07-01",
                                                                "courseEndAt": "2023-09-30",
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "データ無し",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentsResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "データ無し例",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "学生データが見つかりませんでした。",
                                              "details": "学生が見つかりません: ID = 1000000",
                                              "timestamp": "2025-02-11T05:37:37"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "サーバー内部エラー",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "サーバーエラー例",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "エラーが発生しました",
                                              "details": "ERROR",
                                              "timestamp": "2023-11-02 12:34:56"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<StudentsResponse> getAllStudents() {
        try {
            // 学生一覧取得
            List<StudentDetail> students = studentDetailService.findAllStudentDetails();


            return ResponseEntity.ok(new StudentsResponse("学生一覧を取得しました", students));
        } catch (StudentNotFoundException ex) {
            throw new StudentNotFoundException("学生が見つかりませんでした。");
        } catch (Exception ex) {
            throw new RuntimeException("サーバー内部で予期しないエラーが発生しました。", ex);
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
    @Operation(
            summary = "学生情報を削除",
            description = """
                          学生IDを指定し、該当する学生情報を削除します。
                          - 論理削除のため、データ自体は保持されます。
                          """,
            tags = {"学生管理API"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "削除成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDeleteResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "削除成功例",
                                    value = """
                                            {
                                                "message": "学生が削除されました"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "学生が存在しない",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "学生ID未登録例",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "学生データが見つかりませんでした。",
                                              "details": "学生が見つかりません: ID = 1000000",
                                              "timestamp": "2025-02-11T05:37:37"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "データ取得中にエラーが発生しました",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "サーバーエラー例",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "エラーが発生しました",
                                              "details": "ERROR",
                                              "timestamp": "2023-11-02 12:34:56"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<StudentDeleteResponse> removeStudent(@PathVariable @Min(1) Long id) {
        try {
            // 学生情報を削除（存在しない場合は StudentNotFoundException をスロー）
            studentService.deleteStudentById(id);

            // 正常終了時のレスポンス
            return ResponseEntity.ok(new StudentDeleteResponse("学生が削除されました"));
        } catch (StudentNotFoundException ex) {
            throw new StudentNotFoundException("指定された学生IDは見つかりません: ID = " + id);
        } catch (Exception ex) {
            throw new RuntimeException("サーバー内部で予期しないエラーが発生しました。", ex);
        }
    }
}
