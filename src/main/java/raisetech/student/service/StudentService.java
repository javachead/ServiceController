package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentCourseService studentCourseService;

    public StudentService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 学生情報と、それに紐づくコース情報の保存
     */
    @Transactional
    public void saveStudentAndCourses(Student student, List<StudentCourse> courses) {
        // 学生情報を保存（IDは自動生成され、エンティティにセットされる）
        studentRepository.insertStudent(student);

        // 学生IDに関連付けてコース情報を保存
        studentCourseService.saveCourses(courses, student.getId());
    }
}