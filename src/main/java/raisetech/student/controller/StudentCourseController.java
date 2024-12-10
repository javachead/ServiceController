package raisetech.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.data.StudentCourse;
import raisetech.student.service.StudentCourseService;

import java.util.List;
import java.util.Optional;

@RestController
public class StudentCourseController {

    private final StudentCourseService studentCourseService;

    @Autowired
    public StudentCourseController(StudentCourseService studentCourseService) {
        this.studentCourseService = studentCourseService;
    }

    @GetMapping("/studentCourseList")
    public List<StudentCourse> getStudentCourse(@RequestParam(value = "course_name", required = false) Optional<String> courseParam) {
        return courseParam.map(courseName -> studentCourseService.searchStudentListByCourseName(courseName))
                .orElseGet(studentCourseService::getAllstudentCourseService);
    }
}