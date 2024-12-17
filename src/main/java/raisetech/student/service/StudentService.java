package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.controller.converter.StudentConverter;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentCourseService studentCourseService;
    private final StudentConverter converter;

    public StudentService(StudentRepository repository, StudentCourseService studentCourseService, StudentConverter converter) {
        this.repository = repository;
        this.studentCourseService = studentCourseService;
        this.converter = converter;
    }

    public List<StudentDetail> getStudentDetails(int startAge, int endAge) {
        List<Student> students = searchStudentList(startAge, endAge);
        List<StudentCourse> studentCourses = studentCourseService.getAllStudentCourses();
        return students.stream()
                .map(student -> createStudentDetail(student, studentCourses))
                .toList();
    }

    public List<StudentDetail> getStudentDetailsByAgeRange(String ageRange) {
        // 年齢範囲解析 (30S＝ 30歳~39歳)
        if (ageRange == null || ageRange.isEmpty()) {
            throw new IllegalArgumentException("AgeRange cannot be null or empty.");
        }

        if (ageRange.matches("^[1-9]\\d*S$")) {
            int startAge = Integer.parseInt(ageRange.substring(0, ageRange.length() - 1)); // 例: "30S" -> 30
            int endAge = startAge + 9; // 30代 (30-39)
            return getStudentDetails(startAge, endAge);
        } else {
            throw new IllegalArgumentException("Invalid ageRange format. Use formats like '30S', '40S'");
        }
    }

    private List<Student> searchStudentList(int startAge, int endAge) {
        return repository.findByAgeBetween(startAge, endAge);
    }

    private StudentDetail createStudentDetail(Student student, List<StudentCourse> studentCourses) {
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);
        detail.setStudentCourses(converter.convertStudentCourses(student, studentCourses));
        return detail;
    }
}