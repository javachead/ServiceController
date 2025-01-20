package raisetech.student.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raisetech.student.data.Student;
import raisetech.student.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class ApiController {

    private final StudentService studentService;

    public ApiController(StudentService studentService) {
        this.studentService = studentService;
    }

    // 新規作成用のメソッド
    @PostMapping
    public ResponseEntity<String> createStudent(@RequestBody @Valid Student student) {
        studentService.saveStudentAndCourses(student, List.of()); // サービス層で保存処理を実装
        return ResponseEntity.ok("新しい学生が作成されました");
    }

    // 学生情報の更新エンドポイント
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable Long id, @RequestBody @Valid Student student) {
        if (!id.equals(student.getId())) {
            throw new IllegalArgumentException("IDが一致しません");
        }
        studentService.updateStudentDetails(student); // Serviceに処理を委譲
        return ResponseEntity.ok("学生情報を更新しました");
    }
}