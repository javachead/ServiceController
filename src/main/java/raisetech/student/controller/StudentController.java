package raisetech.student.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.domain.StudentDetail;
import raisetech.student.service.StudentService;

import java.util.List;

@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * 年齢範囲や年代指定で学生の詳細リストを取得
     * @param ageRange 年代指定（例: 30S）
     * @return 年代で絞り込まれた学生リスト
     */
    @GetMapping("/studentList")
    public ResponseEntity<List<StudentDetail>> getStudentList(
            //hibernate validator（@Valid、@Pattern）を使用
            @Valid
            @RequestParam(name = "ageRange", required = false)
            @Pattern(regexp = "^[1-9]\\d*S$", message = "AgeRange must be in the format of '30S', '40S', etc.")
            String ageRange
    ) {
        // サービス層に年齢範囲を渡す
        List<StudentDetail> studentDetails = studentService.getStudentDetailsByAgeRange(ageRange);
        return ResponseEntity.ok(studentDetails);
    }
}