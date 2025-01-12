package raisetech.student.service;

import org.springframework.stereotype.Service;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentRepository;
import raisetech.student.service.StudentCourseService;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentDetailService {

    private final StudentRepository studentRepository; // リポジトリ使用
    private final StudentCourseService studentCourseService;

    public StudentDetailService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 学生とそのコース情報を統合して取得
     */
    public List<StudentDetail> findAllStudentDetails() {
        List<Student> students = studentRepository.findAllStudents(); // 全ての学生を取得
        List<StudentDetail> studentDetails = new ArrayList<>();

        for (Student student : students) {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student); // 学生情報

            // 学生に紐づくコース情報を取得
            List<StudentCourse> courses = studentCourseService.findByStudentId(student.getId());
            studentDetail.setStudentCourses(courses); // コース情報を設定

            studentDetails.add(studentDetail); // 学生詳細リストに追加
        }

        return studentDetails;
    }
}