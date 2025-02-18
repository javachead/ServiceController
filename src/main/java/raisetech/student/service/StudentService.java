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
     * 指定されたIDで学生情報を削除する。
     */
    @Transactional
    public void deleteStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("指定された学生が見つかりません: ID=" + studentId));
        studentRepository.deleteById(studentId);
    }

    /**
     * 新しい学生情報を登録＆既存学生の情報を更新します。
     */
    public Student save(Long id, Student student) {
        // IDがnullの場合は新規保存、そうでない場合は更新
        if (student.getId() == null) {
            return studentRepository.save(student);
        } else {
            // 更新の場合
            Student existingStudent = studentRepository.findById(student.getId())
                    .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID=" + student.getId()));

            // 既存データにリクエストのデータを反映
            BeanUtils.copyProperties(student, existingStudent, "id"); // ID以外をコピー
            return studentRepository.save(existingStudent);
        }
    }

    /**
     * 任意フィールドの更新処理をサポート
     */
    private <T> void applyIfPresent(T source, java.util.function.Consumer<T> consumer) {
        Optional.ofNullable(source).ifPresent(consumer);
    }
}
