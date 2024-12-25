package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDetailService {
    private final StudentService studentService;
    private final StudentCourseService studentCourseService;

    public StudentDetailService(StudentService studentService, StudentCourseService studentCourseService) {
        this.studentService = studentService;
        this.studentCourseService = studentCourseService;
    }

    public List<StudentDetail> getStudentDetails() {
        List<Student> students = Optional.ofNullable(studentService.getAllStudents()).orElseGet(ArrayList::new);
        List<StudentCourse> studentCourses = Optional.ofNullable(studentCourseService.getAllStudentCourses()).orElseGet(ArrayList::new);

        List<StudentDetail> studentDetails = new ArrayList<>();
        for (Student student : students) {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            studentDetail.addCourses(studentCourses);
            studentDetails.add(studentDetail);
        }
        return studentDetails;
    }
}