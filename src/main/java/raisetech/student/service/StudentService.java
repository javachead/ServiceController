package raisetech.student.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentRepository;

import java.util.List;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentCourseService studentCourseService;

    public StudentService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    @Transactional
    public void saveStudentAndCourses(@Valid Student student, List<StudentCourse> courses) {
        log.info("学生とコースを保存します。学生ID={}, 学生データ={}", student.getId(), student);

        if (student.getId() == null) {
            log.info("新規の学生を登録: {}", student);
            studentRepository.insertStudent(student);
        } else {
            log.info("学生ID={} の情報を更新", student.getId());
            updateStudentDetails(student.getId(), student);
        }

        studentCourseService.saveCourses(courses, student.getId());
    }

    @Transactional
    public void updateStudentDetails(Long id, @Valid Student updatedStudent) {
        log.info("学生ID={} の情報を更新します。更新データ: {}", id, updatedStudent);

        Student existingStudent = studentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("指定された学生IDが見つかりません: ID=" + id)
        );

        validateName(updatedStudent.getName());
        validateEmail(updatedStudent.getEmail());

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setKanaName(updatedStudent.getKanaName());
        existingStudent.setEmail(updatedStudent.getEmail());
        studentRepository.updateStudentDetails(existingStudent);

        log.info("学生ID={} の情報を更新しました。更新内容: {}", id, existingStudent);
    }

    public Student getStudentById(Long id) {
        log.info("指定されたIDで学生を取得します: ID={}", id);
        return studentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("学生が見つかりません: ID=" + id)
        );
    }

    public List<Student> getAllStudents() {
        log.info("すべての学生情報を取得します");
        List<Student> students = studentRepository.findAllStudents();
        log.info("取得件数: {}", students.size());
        return students;
    }

    @Transactional
    public void deleteStudentById(Long id) {
        log.info("学生ID={} を削除します", id);
        studentRepository.deleteById(id);
        log.info("学生ID={} を削除しました", id);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.error("学生名が無効です: {}", name);
            throw new IllegalArgumentException("学生名は必須です");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.error("メールアドレスが無効です: {}", email);
            throw new IllegalArgumentException("メールアドレスは必須です");
        }
    }

    @Transactional
    public void updateStudent(@Valid Student student) {
        log.info("updateStudentメソッドを実行します。ID={}, データ={}", student.getId(), student);

        // 学生IDがnullの場合は例外をスロー
        if (student.getId() == null) {
            log.error("学生情報の更新にはIDが必要です。");
            throw new IllegalArgumentException("学生情報を更新するためにはIDが必要です。");
        }

        // 既存の`updateStudentDetails`メソッドを再利用
        updateStudentDetails(student.getId(), student);

        log.info("学生情報が正常に更新されました。ID={}", student.getId());
    }
}