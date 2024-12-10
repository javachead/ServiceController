package raisetech.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.data.Students;
import raisetech.student.service.StudentService;

import java.util.List;
import java.util.Optional;

@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/studentList")
    public List<Students> getStudentsByAge(@RequestParam(value = "age", required = false) Optional<String> ageParam) {
        return ageParam.map(age -> {
            switch (age) {
                case "10s":
                    return studentService.getStudentsByAgeRange(10, 19);
                case "20s":
                    return studentService.getStudentsByAgeRange(20, 29);
                case "30s":
                    return studentService.getStudentsByAgeRange(30, 39);
                case "40s":
                    return studentService.getStudentsByAgeRange(40, 49);
                default:
                    return studentService.getAllStudents();
            }
        }).orElseGet(studentService::getAllStudents);
    }
}
