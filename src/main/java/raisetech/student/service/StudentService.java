package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourses;
import raisetech.student.repository.StudentCoursesMapper;
import raisetech.student.repository.StudentMapper;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {

    private final StudentMapper studentMapper;
    private final StudentCoursesMapper studentCoursesMapper;

    public StudentService(StudentMapper studentMapper, StudentCoursesMapper studentCoursesMapper) {
        this.studentMapper = studentMapper;
        this.studentCoursesMapper = studentCoursesMapper;
    }

    @Transactional
    public void saveStudentAndCourses(Student student, List<StudentCourses> courses) {
        // 1. 学生情報を登録し自動生成IDを取得
        studentMapper.insertStudent(student);

        // 2. 学生に関連づけたコース情報を登録
        for (StudentCourses course : courses) {
            course.setStudent(student); // student_idを設定
            studentCoursesMapper.insert(course);
        }
    }
}