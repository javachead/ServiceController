package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        // Repository からデータを取得して返す
        return studentRepository.findAllStudents();
    }
}