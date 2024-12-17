package raisetech.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * @param ageRange 年代指定（30S＝30代）
     * @return 年代で絞り込まれた学生リスト
     */
    @GetMapping("/studentList")
    public ResponseEntity<?> getStudentList(
            @RequestParam(name = "ageRange", required = false) String ageRange
    ) {
        try {
            //入力値のバリデーション
            validateAgeRange(ageRange);

            // サービス層に年齢範囲を渡す
            List<StudentDetail> studentDetails = studentService.getStudentDetailsByAgeRange(ageRange);
            return ResponseEntity.ok(studentDetails);

        } catch (IllegalArgumentException e) {
            // 無効なリクエストパラメータに対するレスポンス
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid ageRange format: " + e.getMessage());
        } catch (Exception e) {
            //  (デバッグ用)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * 年齢範囲文字列のバリデーション
     * @param ageRange 年代指定文字列 (例: 30S)
     * http://localhost:8080/studentList?ageRange=30S
     */
    private void validateAgeRange(String ageRange) {
        if (ageRange == null || ageRange.isBlank()) {
            throw new IllegalArgumentException("AgeRange cannot be null, empty, or blank.");
        }

        if (!ageRange.matches("^[1-9]\\d*S$")) {
            throw new IllegalArgumentException("AgeRange must be in the format of '30S', '40S', etc.");
        }
    }
}