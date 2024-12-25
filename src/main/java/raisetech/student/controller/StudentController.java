package raisetech.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import raisetech.student.controller.converter.StudentConverter;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentController {

    private final StudentService studentService; // StudentServiceをフィールドとして追加
    private final StudentCourseService studentCourseService;
    private final StudentConverter converter;

    @Autowired
    public StudentController(
            StudentService studentService, // StudentServiceをコンストラクタでインジェクション
            StudentCourseService studentCourseService,
            StudentConverter converter
    ) {
        this.studentService = studentService; // インジェクションをフィールドに割り当て
        this.studentCourseService = studentCourseService;
        this.converter = converter;
    }

    /**
     * 学生コースリストを取得
     * @param model データをビューに渡すためのSpringの抽象データ構造
     * @return すべての学生コース情報を含むビュー名
     */
    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        // studentServiceのインスタンスを使用してgetAllStudents()を呼び出す
        List<Student> students = studentService.getAllStudents(); // 修正箇所
        List<StudentCourse> studentCourses = studentCourseService.getAllStudentCourses();

        // 学生詳細リストを作成
        List<StudentDetail> studentDetails = new ArrayList<>();
        for (Student allStudents : students) {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(allStudents);
            List<StudentCourse> convertStudentCourses =
                    converter.convertStudentCourses(allStudents, studentCourses);
            studentDetail.setStudentCourses(convertStudentCourses);
            studentDetails.add(studentDetail);
        }
        model.addAttribute("studentList", studentDetails); // ビューへデータを追加
        return "studentList";
    }
}