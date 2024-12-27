package raisetech.student.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import raisetech.student.service.StudentDetailService;
import raisetech.student.domain.StudentDetail;
import java.util.List;

@Controller
public class StudentController {
    private final StudentDetailService studentDetailService;

    public StudentController(StudentDetailService studentDetailService) {
        this.studentDetailService = studentDetailService;
    }

    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        List<StudentDetail> studentDetails = studentDetailService.getStudentDetails();
        model.addAttribute("studentList", studentDetails);
        return "studentList";
    }
}