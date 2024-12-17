//package raisetech.student.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import raisetech.student.data.StudentCourse;
//import raisetech.student.service.StudentCourseService;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//public class StudentCourseController {
//
//    private final StudentCourseService studentCourseService;
//
//    @Autowired
//    public StudentCourseController(StudentCourseService studentCourseService) {
//        this.studentCourseService = studentCourseService;
//    }
//
//    /**
//     * コース名で絞り込みまたは全リストを取得する
//     *
//     * @param courseParam コース名（リクエストパラメータ、任意）
//     * @return 学生コースリスト
//     */
//    @GetMapping("/studentCourseList")
//    public List<StudentCourse> getStudentCourse(@RequestParam(value = "course_name", required = false) String courseName) {
//        if (courseName != null && !courseName.isEmpty()) {
//            // クエリに基づいて検索
//            return studentCourseService.searchStudentListByCourseName(courseName);
//        } else {
//            // コース名が指定されていない場合は全データを取得
//            return studentCourseService.getAllStudentCourses();
//        }
//    }