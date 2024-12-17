package raisetech.student.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;
    @Setter
    @Getter
    private String student_Course;

    @Autowired
    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    /**
     * 指定したIDの学生コースデータを取得
     * @param id コースID
     * @return Optionalでラップされたコースデータ
     */
    public Optional<StudentCourse> getStudentCourseById(Long id) {
        return studentCourseRepository.findById(id);
    }
    public List<StudentCourse> getAllStudentCourses() {
        return studentCourseRepository.findAll();
    }
}