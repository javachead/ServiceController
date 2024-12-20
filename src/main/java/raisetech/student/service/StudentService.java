package raisetech.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.data.Students;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private static StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        StudentService.studentRepository = studentRepository;
    }

    public static
    List<Students> getAllStudents() {
        return studentRepository.findAll(); // 静的メソッドを廃止
    }
}