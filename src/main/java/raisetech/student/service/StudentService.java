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

    // コンストラクタで依存性注入を一元化
    public StudentService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 学生データとコース情報を保存
     */
    @Transactional
    public int saveStudentAndCourses(@Valid Student student, List<StudentCourse> courses) {
        log.info("saveStudentAndCourses メソッド開始。学生ID: {}, 学生データ: {}", student.getId(), student);

        int rowsUpdated = 0;

        if (student.getId() == null) {
            log.info("新規の学生を作成します。データ: {}", student);
            studentRepository.insertStudent(student);
            rowsUpdated = 1;
        } else {
            log.info("既存の学生情報を更新します。学生ID: {}", student.getId());

            // この部分で実行されているか確認
            rowsUpdated = studentRepository.updateStudentDetails(student);

            // クエリ呼び出し後にログを追加
            log.info("Update クエリが実行されました。更新件数: {}, 学生ID: {}", rowsUpdated, student.getId());
        }

        log.info("学生のコースを保存します。学生ID: {}", student.getId());
        studentCourseService.saveCourses(courses, Long.valueOf(student.getId()));

        log.info("saveStudentAndCourses メソッド終了。");
        return rowsUpdated;
    }

    @Transactional
    public void updateStudentById(Long id, @Valid Student student) {
        log.info("updateStudentByIdメソッドが呼び出されました。更新対象ID: {}, 更新データ: {}", id, student);

        // 学生データの検証
        validateStudent(student);

        try {
            log.info("SQL 実行開始: 更新対象の学生ID: {}", id);
            int rowsUpdated = studentRepository.updateStudentDetails(student); // 更新を実行
            logResult(rowsUpdated, id);
        } catch (Exception e) {
            log.error("学生情報の更新に失敗しました。ID: {}", id, e);
            throw new RuntimeException("学生情報の更新中にエラーが発生しました: " + e.getMessage(), e);
        }

        log.info("updateStudentByIdメソッドが完了しました。更新対象ID: {}", id);
    }

    public Student getStudentById(Long id) {
        log.info("getStudentByIdメソッドが呼び出されました。取得対象の学生ID: {}", id);

        Student student = studentRepository.findById(id).orElseThrow(() -> {
            log.warn("指定されたIDの学生情報が見つかりません。ID: {}", id);
            return new RuntimeException("学生情報が見つかりません。ID: " + id);
        });

        log.info("学生情報が見つかりました。学生ID: {}, 学生データ: {}", id, student);
        return student;
    }

    @Transactional
    public void deleteStudentById(Long id) {
        log.info("deleteStudentByIdメソッドが呼び出されました。削除対象の学生ID: {}", id);

        studentRepository.deleteById(id);

        log.info("学生情報が正常に削除されました。学生ID: {}", id);
    }

    public List<Student> getAllStudents() {
        log.info("getAllStudentsメソッドが呼び出されました。すべての学生情報を取得します。");

        List<Student> students = studentRepository.findAllStudents();

        log.info("すべての学生情報を取得しました。取得件数: {}", students.size());
        return students;
    }

    @Transactional
    public void updateStudent(@Valid Student student) {
        log.info("updateStudentメソッドが呼び出されました。更新データ: {}", student);

        validateStudent(student);

        try {
            log.info("SQL 実行: 更新する学生ID: {}", student.getId());
            int rowsUpdated = studentRepository.updateStudentDetails(student); // 更新を実行
            logResult(rowsUpdated, Long.valueOf(student.getId()));
        } catch (Exception e) {
            log.error("学生データの更新に失敗しました。学生ID: {}", student.getId(), e);
            throw new RuntimeException("学生データの更新中にエラーが発生しました: " + e.getMessage(), e);
        }

        log.info("updateStudentメソッドが正常に完了しました。学生ID: {}", student.getId());
    }

    // 学生情報の検証メソッド
    private void validateStudent(Student student) {
        log.debug("validateStudentメソッドが呼び出されました。検証対象: {}", student);

        if (student.getName() == null || student.getName().isEmpty()) {
            log.error("検証エラー: 学生の名前が空または null です。");
            throw new IllegalArgumentException("学生の名前は必須です。");
        }

        log.debug("学生データの検証が正常に完了しました。検証対象: {}", student);
    }

    // 更新結果のログ出力
    private void logResult(int rowsUpdated, Long id) {
        if (rowsUpdated > 0) {
            log.info("学生データが更新されました。ID: {}, 更新件数: {}", id, rowsUpdated);
        } else {
            log.warn("学生データは更新されませんでした。ID: {}", id);
        }
    }
}