package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourses;
import raisetech.student.repository.StudentCoursesMapper;

import java.util.List;

@Service
@Transactional
public class StudentCourseService {

    private final StudentCoursesMapper studentCoursesMapper;

    public StudentCourseService(StudentCoursesMapper studentCoursesMapper) {
        this.studentCoursesMapper = studentCoursesMapper;
    }

    public List<StudentCourses> findByStudentId(Long id) { // メソッド名を修正
        return studentCoursesMapper.findByStudentId(id); // Mapperのメソッド名と一致させる
    }
}