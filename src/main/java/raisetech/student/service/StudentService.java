package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        // studentRepositoryがnullを返す場合に備えてOptionalでラップ
        return Optional.ofNullable(studentRepository)
                .map(StudentRepository::findAll)
                .orElseThrow(() -> new IllegalStateException("StudentRepository isn’t available"));
    }
}