package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import raisetech.student.data.Student;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.repository.StudentRepository;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentCourseService studentCourseService;

    // コンストラクタ（依存性注入）
    public StudentService(StudentRepository studentRepository, StudentCourseService studentCourseService) {
        this.studentRepository = studentRepository;
        this.studentCourseService = studentCourseService;
    }

    /**
     * 学生情報をIDで取得するメソッド。
     * @param id 学生ID
     * @return 指定されたIDに一致する学生情報
     * @throws StudentNotFoundException 該当する学生が存在しない場合
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID = " + id));
    }

    /**
     * 学生情報を更新するメソッド。
     * @param updatedStudent 更新対象の学生オブジェクト
     * @throws StudentNotFoundException 該当する学生が存在しない場合
     */
    public void updateStudent(Student updatedStudent) {
        // 更新対象の学生を取得。該当しない場合は例外をスロー
        Student existingStudent = studentRepository.findById(updatedStudent.getId())
                .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID=" + updatedStudent.getId()));

        // BeanUtils.copyProperties の適用
        BeanUtils.copyProperties(updatedStudent, existingStudent, "id");

        // applyIfPresent による任意フィールドの更新処理共通化
        applyIfPresent(updatedStudent.getNickname(), existingStudent::setNickname);
        applyIfPresent(updatedStudent.getSex(), existingStudent::setSex);
        applyIfPresent(updatedStudent.getRemark(), existingStudent::setRemark);

        // id以外の特殊処理(isDeleted専用の設定)
        existingStudent.setDeleted(updatedStudent.getDeleted());

        // 更新後のデータを保存する
        studentRepository.save(existingStudent);
    }

    /**
     * 共通メソッド: 任意フィールドの更新処理をサポート
     * @param source ソース値。nullかどうか判定される。
     * @param consumer ソース値を処理する対象フィールドのメソッド参照もしくはラムダ式
     * @param <T> ソース値の型
     */
    private <T> void applyIfPresent(T source, java.util.function.Consumer<T> consumer) {
        Optional.ofNullable(source).ifPresent(consumer);
    }

    /**
     * 指定されたIDで学生情報を削除する。
     * @param studentId 削除対象の学生ID
     * @throws StudentNotFoundException 該当する学生が存在しない場合
     */
    public void deleteStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("指定された学生が見つかりません: ID=" + studentId));
        studentRepository.deleteById(studentId);
    }

    /**
     * 学生情報およびコース情報を保存または更新するメソッド。
     * @param student 保存対象の学生情報
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveStudentAndCourses(Student student) {
        saveStudent(student);       // 学生情報の保存または更新
        saveCourses(student); // 関連コースの保存または更新
    }

    /**
     * 学生情報を保存または更新するプライベートメソッド。
     * @param student 更新または保存する学生情報
     */
    private void saveStudent(Student student) {
        Optional.ofNullable(student.getId())
                .ifPresentOrElse(
                        id -> updateStudent(student), // IDがあれば更新処理
                        () -> studentRepository.save(student) // IDがなければ新規登録
                );
    }

    /**
     * 学生に関連するコース情報を保存または更新するメソッド。
     * @param student 保存または更新対象の学生情報
     */
    private void saveCourses(Student student) {
        Optional.ofNullable(student.getStudentCourses())
                .filter(courses -> !courses.isEmpty()) // 空でない場合のみ処理
                .ifPresent(courses -> studentCourseService.saveCourses(courses, student.getId()));
    }
}
