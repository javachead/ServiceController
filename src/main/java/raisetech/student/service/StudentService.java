package raisetech.student.service;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.repository.StudentRepository;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
    }

    /**
     * 学生情報を更新するメソッド。
     * 注意:
     * - 更新時に、学生ID（`student.getId()`）が必須です。存在しない場合は例外がスローされます。
     * - 名前とメールアドレスには検証ロジックが適用されます。
     * @param id 学生ID
     * @param student 学生の更新用データ
     */
    @Transactional
    public void updateStudentDetails(Long id, @Valid Student student) {
        Optional.ofNullable(student.getId())
                .orElseThrow(() -> {
                    log.error("学生情報の更新にはIDが必要です。");
                    return new IllegalArgumentException("学生情報を更新するためにはIDが必要です。");
                });

        log.info("updateStudentDetailsを実行します。ID={}, データ={}", student.getId(), student);

        Optional.ofNullable(student.getName())
                .filter(name -> !name.trim().isEmpty())
                .orElseThrow(() -> {
                    log.error("学生名が無効です: {}", student.getName());
                    return new IllegalArgumentException("学生名は必須です");
                });

        Optional.ofNullable(student.getEmail())
                .filter(email -> !email.trim().isEmpty())
                .orElseThrow(() -> {
                    log.error("メールアドレスが無効です: {}", student.getEmail());
                    return new IllegalArgumentException("メールアドレスは必須です");
                });

        studentRepository.updateStudentDetails(student);

        log.info("学生ID={} の情報を更新しました。更新内容: {}", student.getId(), student);
    }

    /**
     * 指定されたIDで学生情報を削除する。
     * - `deleteById` メソッドの動作は物理削除である点に注意。
     * - 削除対象の学生に紐づくコース情報が自動的に削除されるかどうかはリポジトリ側の実装に依存します。
     * @param id 削除対象の学生ID
     */
    @Transactional
    public void deleteStudentById(Long id) {
        log.info("学生ID={} を削除します", id);
        studentRepository.deleteById(id);
        log.info("学生ID={} を削除しました", id);
    }

    /**
     * IDが空の場合は新規作成、存在する場合は更新処理を行う。
     * - 新規作成時は挙動がシンプルで変更点なし。
     * - 更新時には `updateStudentDetails` を介するため、検証ロジックやロギングが一貫する。
     * @param student 保存対象の学生情報
     */
    public void saveStudent(Student student) {
        Optional.ofNullable(student.getId())
                .ifPresentOrElse(
                        id -> updateStudentDetails(id, student), // IDがある場合は更新
                        () -> studentRepository.insertStudent(student) // IDが空の場合は新規作成
                );
    }

    /**
     * 指定されたIDに対応する学生情報を取得する。
     * 注意:
     * - 学生情報が見つからない場合、または削除フラグが立っている場合は例外がスローされます。
     * @param id 学生ID
     * @return 学生情報
     */
    public Student getStudentById(Long id) {
        log.info("学生ID={} の情報を取得します", id);

        return studentRepository.findById(id)
                .filter(student -> !student.isDeleted()) // 削除フラグが立っていないか確認
                .orElseThrow(() -> {
                    log.warn("指定されたIDの学生が見つかりません。または削除されています。ID: {}", id);
                    // カスタム例外をスローするように変更
                    return new StudentNotFoundException("指定されたIDの学生が見つかりません: ID=" + id);
                });
    }
}