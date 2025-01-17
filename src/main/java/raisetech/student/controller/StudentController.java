package raisetech.student.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        // 学生情報とコース情報を保存
        Student student = studentDetail.getStudent();
        List<StudentCourse> courses = studentDetail.getStudentCourses();
        studentService.saveStudentAndCourses(student, courses);

        logger.info("学生情報とコースを登録しました: {} ", student.getName());

        return "redirect:/studentList";
    }

    // ---------------- 一覧取得 ----------------

    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        // 学生詳細情報を取得
        List<StudentDetail> studentDetails = studentDetailService.findAllStudentDetails();

        // Nullチェックしてからモデルへ追加
        if (studentDetails != null && !studentDetails.isEmpty()) {
            model.addAttribute("studentList", studentDetails);
            logger.info("学生一覧を取得しました。データ件数: {}", studentDetails.size());
        } else {
            logger.warn("学生詳細データが空です。");
            model.addAttribute("errorMessage", "データが存在しません。");
        }

        return "studentList";
    }

    // ---------------- 詳細表示と更新確認 ----------------

    // GET: 更新フォームを取得するエンドポイント
    @GetMapping("/updateStudentDetail")
    public String getUpdateStudentForm(@RequestParam("id") Long id, Model model) {
        // 指定された学生IDの情報を取得
        Student student = studentService.getStudentById(id);

        // 学生が見つからない場合の処理
        if (student == null) {
            logger.warn("指定されたIDの学生が見つかりません。ID: {}", id);
            model.addAttribute("errorMessage", "指定されたIDの学生が見つかりません。");
            return "error"; // エラー画面表示
        }

        // 取得した学生情報をモデルに追加
        model.addAttribute("student", student);
        return "updateStudentDetail"; // 更新フォームのHTMLページを返す
    }

    // ---------------- 学生情報更新 ----------------

    @PostMapping("/updateStudentDetail")
    public String updateStudent(@Valid @ModelAttribute("student") Student student,
                                BindingResult result, Model model) {
        // バリデーションエラーがある場合
        if (result.hasErrors()) {
            logger.warn("バリデーションエラー: {}", result.getFieldErrors());
            return "updateStudentDetail";
        }

        // 学生情報を更新
        studentService.updateStudent(student);
        logger.info("学生情報を更新しました。学生: {}", student.getId());

        // 更新完了後、学生リストページへリダイレクト
        return "redirect:/studentList";
    }

    // ---------------- 学生削除 ----------------

    @PostMapping("/deleteStudent")
    public String deleteStudent(@RequestParam("id") Long id) {
        // 指定されたIDで学生を削除
        studentService.deleteStudentById(id);
        logger.info("指定された学生を削除しました。ID: {}", id);

        // 学生リストページにリダイレクト
        return "redirect:/studentList";
    }

    @PostMapping("/updateStudent")
    public String updateStudentPost(@Valid @ModelAttribute("student") Student student,
                                    BindingResult result, Model model) {
        // バリデーションエラーがある場合はフォームページを再表示
        if (result.hasErrors()) {
            logger.warn("バリデーションエラー: {}", result.getFieldErrors());
            return "updateStudentDetail";
        }

        // 学生情報をサービス層で更新
        studentService.updateStudent(student);
        logger.info("学生情報を更新しました。ID: {}", student.getId());

        // 正常終了後、学生一覧画面へリダイレクト
        return "redirect:/studentList";
    }
}