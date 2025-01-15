package raisetech.student.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PatchMapping;

import jakarta.validation.Valid;

import java.util.List;

import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

@Controller
public class StudentController {

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentDetailService studentDetailService;
    private final StudentService studentService;
    private final StudentCourseService studentCourseService;

    // コンストラクタによる依存性注入
    public StudentController(StudentDetailService studentDetailService,
                             StudentService studentService,
                             StudentCourseService studentCourseService) {
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
        this.studentCourseService = studentCourseService;
    }

    // ---------------- 新規登録 ----------------

    @GetMapping("/newStudent")
    public String newStudent(Model model) {
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(new Student());

        // 初期状態で空のコースを1件だけ追加
        studentDetail.addCourse(new StudentCourse());
        model.addAttribute("studentDetail", studentDetail);

        return "registerStudent";
    }

    @PostMapping("/registerStudent")
    public String registerStudent(@ModelAttribute("studentDetail") @Valid StudentDetail studentDetail,
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.warn("バリデーションエラーが発生しました: {}", result.getFieldErrors());
            model.addAttribute("studentDetail", studentDetail);
            return "registerStudent";
        }

        try {
            Student student = studentDetail.getStudent();
            List<StudentCourse> courses = studentDetail.getStudentCourses();

            // サービスを通して保存
            studentService.saveStudentAndCourses(student, courses);

            logger.info("学生情報とコースを登録しました: {} ", student.getName());
        } catch (Exception e) {
            logger.error("学生情報登録中にエラー発生", e);
            model.addAttribute("errorMessage", "登録中にエラーが発生しました。");
            return "registerStudent";
        }

        // 正常終了
        return "redirect:/studentList";
    }

    // ---------------- 一覧取得 ----------------

    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        try {
            List<StudentDetail> studentDetails = studentDetailService.findAllStudentDetails();

            // Null チェックしてからモデルへ追加
            if (studentDetails != null && !studentDetails.isEmpty()) {
                model.addAttribute("studentList", studentDetails);
                logger.info("学生一覧を取得しました。データ件数: {}", studentDetails.size());
            } else {
                logger.warn("学生詳細データが空です。");
                model.addAttribute("errorMessage", "データが存在しません。");
            }
        } catch (Exception e) {
            logger.error("学生リスト取得中にエラー発生", e);
            return "error"; // エラー画面
        }

        return "studentList";
    }

    // ---------------- 詳細表示と更新確認 ----------------

    // GET: 更新フォームを取得するエンドポイント
    @GetMapping("/updateStudentDetail")
    public String getUpdateStudentForm(@RequestParam("id") Long id, Model model) {
        try {
            // 指定された学生IDの情報を取得
            Student student = studentService.getStudentById(id);

            if (student == null) {
                logger.warn("指定されたIDの学生が見つかりません。ID: {}", id);
                model.addAttribute("errorMessage", "指定されたIDの学生が見つかりません。");
                return "error"; // エラー画面表示
            }

            // 取得した学生情報をモデルに追加
            model.addAttribute("student", student);
            return "updateStudentDetail"; // 更新フォームのHTMLページを返す
        } catch (Exception e) {
            logger.error("学生情報取得中にエラー発生。ID: {}", id, e);
            model.addAttribute("errorMessage", "サーバー内でエラーが発生しました。");
            return "error"; // エラー画面
        }
    }

    // ---------------- 学生情報更新 ----------------

    @PostMapping("/updateStudentDetail")
    public String updateStudent(@Valid @ModelAttribute("student") Student student,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.warn("バリデーションエラー: {}", result.getFieldErrors());
            return "updateStudentDetail";
        }

        try {
            studentService.updateStudent(student);
            logger.info("学生情報を更新しました。学生: {}", student.getId());
        } catch (Exception e) {
            logger.error("学生情報更新中にエラー発生", e);
            model.addAttribute("errorMessage", "更新中にエラーが発生しました。");
            return "updateStudentDetail";
        }

        return "redirect:/studentList";
    }

    // ---------------- 学生削除 ----------------

    @PostMapping("/deleteStudent")
    public String deleteStudent(@RequestParam("id") Long id, Model model) {
        try {
            studentService.deleteStudentById(id);
            logger.info("指定された学生を削除しました。ID: {}", id);
        } catch (Exception e) {
            logger.error("学生削除処理中にエラー: ID: {}", id, e);
            model.addAttribute("errorMessage", "削除処理中にエラーが発生しました。");
            return "error";
        }

        return "redirect:/studentList";
    }

    @PostMapping("/updateStudent")
    public String updateStudentPost(@Valid @ModelAttribute("student") Student student,
                                    BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.warn("バリデーションエラー: {}", result.getFieldErrors());
            return "updateStudentDetail"; // 再びフォームページを表示
        }

        try {
            // サービス層で学生情報を更新
            studentService.updateStudent(student);
            logger.info("学生情報を更新しました。ID: {}", student.getId());
        } catch (Exception e) {
            logger.error("学生情報の更新中にエラーが発生しました: {}", student.getId(), e);
            model.addAttribute("errorMessage", "学生情報の更新中にエラーが発生しました。");
            return "updateStudentDetail"; // エラーメッセージを表示するフォーム
        }

        // 正常終了後、学生一覧画面へリダイレクト
        return "redirect:/studentList";
    }

    // ---------------- APIエンドポイント ----------------

    @PatchMapping("/api/student/{id}")
    @ResponseBody
    public ResponseEntity<Object> updateStudentApi(@PathVariable Long id,
                                                   @RequestBody @Valid Student student,
                                                   BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            logger.warn("バリデーションエラー: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            studentService.updateStudent(student);
            logger.info("学生情報をAPIで更新しました。ID: {}", id);
            return ResponseEntity.ok("更新が成功しました。");
        } catch (IllegalArgumentException e) {
            logger.error("引数エラー", e);
            return ResponseEntity.badRequest().body("エラー: " + e.getMessage());
        } catch (Exception e) {
            logger.error("更新中に予期しないエラー", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("サーバーエラーが発生しました。");
        }
    }
}