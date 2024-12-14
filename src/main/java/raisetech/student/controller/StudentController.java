package raisetech.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.controller.converter.StudentConverter;
import raisetech.student.data.StudentCourse;
import raisetech.student.data.Students;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentService;

import java.util.ArrayList;
import java.util.List;

//学生のコース情報を管理するコントローラークラス

@RestController
public
class StudentController{

    private final StudentCourseService studentCourseService;
    private final StudentConverter converter;

    @Autowired
    public
    StudentController(StudentCourseService studentCourseService, StudentConverter converter) {
        this.studentCourseService = studentCourseService;
        this.converter = converter;
    }

    /**
     * 学生コースリストを取得
     *
     * @return 全ての学生コース情報
     */
    @GetMapping("/studentList")
    public
    List<StudentDetail> getStudentList() {

        // 全学生データとコースデータを取得
        List<Students> students = StudentService.getAllStudents();
        List<StudentCourse> studentCourses = studentCourseService.getAllStudentCourses();

        // 学生詳細リストを作成
        List<StudentDetail> studentDetails = new ArrayList<>();
        for (Students student : students) {

            // 学生詳細データ作成
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            // Converterを利用して学生に紐づくコースデータを取得
            List<StudentCourse> convertStudentCourses = converter.convertStudentCourses(student, studentCourses);

            studentDetail.setStudentCourses(convertStudentCourses);
            studentDetails.add(studentDetail);
        }
        return studentDetails;
    }
}