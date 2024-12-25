package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.util.List;

@Service
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    public List<StudentCourse> getAllStudentCourses() {
        // Repository からデータを取得して返す
        return studentCourseRepository.findAllStudentCourses();
    }
}