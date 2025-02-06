package raisetech.student.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.repository.StudentRepository;

import java.util.Optional;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * 学生情報をIDで取得するメソッド。
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID = " + id));
    }

    /**
     * 学生情報を更新するメソッド（デフォルトのトランザクション）。
     */
    public void updateStudent(Student updatedStudent) {
        // 学生情報を取得し、存在しない場合の例外
        Student existingStudent = studentRepository.findById(updatedStudent.getId())
                .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID=" + updatedStudent.getId()));

        // IDのみコピー対象から除外してプロパティを一括コピー
        BeanUtils.copyProperties(updatedStudent, existingStudent, "id");

        // エンティティを保存
        studentRepository.save(existingStudent);
    }

    /**
     * 指定されたIDで学生情報を削除する。
     */
    @Transactional
    public void deleteStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("指定された学生が見つかりません: ID=" + studentId));
        studentRepository.deleteById(studentId);
    }

    /**
     * 新しい学生情報を登録します。
     */
    public Student save(Student newStudent) {
        if (newStudent.getId() != null) {
            throw new IllegalArgumentException("新規登録にはIDを指定しないでください: ID = " + newStudent.getId());
        }
        log.info("新しい学生情報を登録します: {}", newStudent);
        return studentRepository.save(newStudent);
    }

    /**
     * 任意フィールドの更新処理をサポート
     */
    private <T> void applyIfPresent(T source, java.util.function.Consumer<T> consumer) {
        Optional.ofNullable(source).ifPresent(consumer);
    }
}
