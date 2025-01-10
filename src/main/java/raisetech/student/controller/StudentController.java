package raisetech.student.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourses;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import jakarta.validation.Valid;
import java.util.List;

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
        this.studentCourseService = studentCourseService;  // 正しい依存性注入
    }

    // 学生情報一覧を取得して表示
    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        List<StudentDetail> studentDetails = studentDetailService.findAllStudentDetails();

        // 学生ごとのコース情報を追加
        studentDetails.forEach(studentDetail -> {
            Long studentId = studentDetail.getStudent().getId(); // 学生IDを取得
            List<StudentCourses> courses = studentCourseService.findByStudentId(studentId); // コース情報を取得
            studentDetail.setStudentCourses(courses); // 学生の詳細にコース情報を設定
        });

        model.addAttribute("studentList", studentDetails); // 学生リストをモデルに追加
        return "studentList";
    }

   // 新規学生登録画面の表示
    @GetMapping("/newStudent")
    public String newStudent(Model model) {
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(new Student());

        // 初期状態で空のコースを1件だけセット
        studentDetail.addCourse(new StudentCourses());
        model.addAttribute("studentDetail", studentDetail);

        return "registerStudent";
    }

    //指定された学生IDに基づくコース情報を取得
    @GetMapping("/{id}")
    public List<StudentCourses> getCoursesByStudentId(@PathVariable Long id) {
        return studentCourseService.findByStudentId(id);
    }

    //新規学生情報の登録処理
    @PostMapping("/registerStudent")
    public String registerStudent(@ModelAttribute("studentDetail") @Valid StudentDetail studentDetail,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("studentDetail", studentDetail);
            return "registerStudent";
        }

        try {
            Student student = studentDetail.getStudent();
            List<StudentCourses> courses = studentDetail.getStudentCourses();

            // メソッド名を修正: saveStudentAndCourses を呼び出す
            studentService.saveStudentAndCourses(student, courses);

            logger.info("学生情報とコース情報を登録しました: {}", student.getName());
        } catch (Exception e) {
            logger.error("学生情報の登録中にエラーが発生しました", e);
            model.addAttribute("errorMessage", "登録中にエラーが発生しました。");
            return "registerStudent";
        }

        return "redirect:/studentList";
    }
}