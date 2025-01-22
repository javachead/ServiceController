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
    public void updateStudentDetails(Long id, @Valid Student student) {
        if (student.getId() == null) {
            log.error("学生情報の更新にはIDが必要です。");
            throw new IllegalArgumentException("学生情報を更新するためにはIDが必要です。");
        }

        log.info("updateStudentDetailsを実行します。ID={}, データ={}", student.getId(), student);

        // インプットされた Student データを直接利用して更新する
        validateName(student.getName());
        validateEmail(student.getEmail());

        studentRepository.updateStudentDetails(student);

        log.info("学生ID={} の情報を更新しました。更新内容: {}", student.getId(), student);
    }

    @Transactional
    public void deleteStudentById(Long id) {
        log.info("学生ID={} を削除します", id);
        studentRepository.deleteById(id);
        log.info("学生ID={} を削除しました", id);
    }

    // 学生情報を保存
    public void saveStudent(Student student) {
        // IDがnullなら新規作成、そうでなければ更新
        if (student.getId() == null) {
            studentRepository.insertStudent(student); // 学生を新規作成
        } else {
            updateStudentDetails(student.getId(), student); // 学生情報の更新処理
        }
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

    public Student getStudentById(Long id) {
        log.info("学生ID={} の情報を取得します", id);

        // リポジトリから学生を取得
        Student student = studentRepository.findById(id).orElse(null);

        // データが見つからない場合の処理
        if (student == null || student.isDeleted()) {
            log.warn("指定されたIDの学生が見つかりません。または削除されています。ID: {}", id);
            throw new IllegalArgumentException("指定されたIDの学生が見つかりません: ID=" + id);
        }

        log.info("学生情報が見つかりました: ID={}, データ={}", id, student);
        return student;
    }
}