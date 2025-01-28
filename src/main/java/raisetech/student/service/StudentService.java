package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid; // Controllerで利用
import lombok.extern.slf4j.Slf4j;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.repository.StudentRepository;

import java.util.List;
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
        Student existingStudent = studentRepository.findById(updatedStudent.getId())
                .orElseThrow(() -> new StudentNotFoundException("学生が見つかりません: ID=" + updatedStudent.getId()));

        // 定義済みフィールドの更新
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setAge(updatedStudent.getAge());
        existingStudent.setKanaName(updatedStudent.getKanaName());
        existingStudent.setArea(updatedStudent.getArea());

        // 任意フィールドの更新（Optional を活用して null チェックを簡略化）
        Optional.ofNullable(updatedStudent.getNickname()).ifPresent(existingStudent::setNickname);
        Optional.ofNullable(updatedStudent.getSex()).ifPresent(existingStudent::setSex);
        Optional.ofNullable(updatedStudent.getRemark()).ifPresent(existingStudent::setRemark);

        // isDeleted フィールドの直接設定
        existingStudent.setDeleted(updatedStudent.isDeleted());

        // 更新後のデータを保存する
        studentRepository.save(existingStudent);
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
    public void saveStudentAndCourses(Student student) {
        saveOrUpdateStudent(student);       // 学生情報の保存または更新
        saveOrUpdateStudentCourses(student); // 関連コースの保存または更新
    }

    /**
     * 学生情報を保存または更新するプライベートメソッド。
     * @param student 更新または保存する学生情報
     */
    private void saveOrUpdateStudent(Student student) {
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
    private void saveOrUpdateStudentCourses(Student student) {
        Optional.ofNullable(student.getStudentCourses())
                .filter(courses -> !courses.isEmpty()) // 空でない場合のみ処理
                .ifPresent(courses -> studentCourseService.saveCourses(courses, student.getId()));
    }
}