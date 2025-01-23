package raisetech.student.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private final StudentCourseService studentCourseService;
    private final StudentDetailService studentDetailService;
    private final StudentService studentService;

    // コンストラクタによる依存性注入
    public StudentController(StudentCourseService studentCourseService,
                             StudentDetailService studentDetailService,
                             StudentService studentService) {
        this.studentCourseService = studentCourseService;
        this.studentDetailService = studentDetailService;
        this.studentService = studentService;
    }

    // ---------------- 学生登録 ----------------
    @PostMapping
    public ResponseEntity<?> addStudentWithCourses(@RequestBody @Valid StudentDetail studentDetail) {
        // `Student`情報を登録
        Student student = studentDetail.getStudent();
        studentService.saveStudent(student);

        // `StudentCourses`情報を登録
        List<StudentCourse> studentCourses = studentDetail.getStudentCourses();
        studentCourses.forEach(course -> {
            course.setStudentId(student.getId());
            studentCourseService.saveStudentCourse(course);
        });

        // レスポンス
        Map<String, Object> response = new HashMap<>();
        response.put("message", "新しい学生とコースが登録されました");
        response.put("student", student);
        response.put("studentCourses", studentCourses);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------- 全学生一覧取得 ----------------
    @GetMapping
    public ResponseEntity<?> getStudentList() {
        List<StudentDetail> studentDetails = studentDetailService.findAllStudentDetails();

        Map<String, Object> response = new HashMap<>();

        if (studentDetails == null || studentDetails.isEmpty()) {
            response.put("message", "学生データが存在しません");
            response.put("data", Collections.emptyList());
            return ResponseEntity.ok(response);
        }

        response.put("message", "学生一覧を取得しました");
        response.put("data", studentDetails);
        return ResponseEntity.ok(response);
    }

    // ---------------- 個別学生取得 ----------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);

        if (student == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "指定された学生データが見つかりません");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "指定されたIDの学生を取得しました");
        response.put("data", student);
        return ResponseEntity.ok(response);
    }

    // ---------------- 学生コース一覧取得 ----------------
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getStudentCourses(@PathVariable Long id) {
        List<StudentCourse> courses = studentCourseService.findByStudentId(id);

        Map<String, Object> response = new HashMap<>();

        if (courses == null || courses.isEmpty()) {
            response.put("message", "学生に紐付くコースが見つかりません");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", "学生のコース一覧を取得しました");
        response.put("data", courses);
        return ResponseEntity.ok(response);
    }

    // ---------------- 学生情報更新 ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody @Valid StudentDetail studentDetail) {
        Student student = studentDetail.getStudent();

        if (student == null || !student.getId().equals(id)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "無効な学生情報です");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        studentService.updateStudentDetails(id, student);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "学生情報が更新されました");
        return ResponseEntity.ok(response);
    }

    // ---------------- 学生削除 ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);

        if (student == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "指定された学生が見つかりません");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        studentService.deleteStudentById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "学生が削除されました");
        return ResponseEntity.ok(response);
    }

    public Logger getLogger() {
        return logger;
    }
}