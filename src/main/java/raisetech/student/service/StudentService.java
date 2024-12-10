package raisetech.student.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.Repository.StudentRepository;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生情報とコース情報を管理するサービスクラス
 */
@Service
public class StudentService {

    // リポジトリインスタンス
    private final StudentRepository repository;

    /**
     * コンストラクタ
     *
     * @param repository 注入されるStudentRepositoryのインスタンス
     */
    @Autowired
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    /**
     * このメソッドはデータベースから全ての学生情報を取得し、その中から年齢が30代の学生のみをフィルタリングして返します。
     *
     * @return 年齢が30代の学生リスト
     */
    public List<Student> searchStudentList() {
        // データベースから全学生を取得
        List<Student> students = repository.search();

        // 年齢が30代の学生のみを抽出して返す
        return students.stream()
                .filter(student -> student.getAge() >= 30 && student.getAge() < 40)
                .collect(Collectors.toList());
    }

    /**
     * 「Java」コースの学生コース情報リストを取得します。
     * このメソッドはデータベースから全ての学生コース情報を取得し、その中からコース名が"Java"である情報のみをフィルタリングして返します。
     *
     * @return コース名が「Java」である学生コース情報リスト
     */
    public List<StudentCourse> searchCourseList() {
        // データベースから全学生コースを取得
        List<StudentCourse> courses = repository.searchCourseList();

        // 「Java」コースの学生コースのみを抽出して返す
        return courses.stream()
                .filter(course -> "Java".equalsIgnoreCase(course.getCourseName()))
                .collect(Collectors.toList());
    }

    /**
     * StudentRepositoryのゲッター
     *
     * @return リポジトリインスタンス
     */
    public StudentRepository getRepository() {
        return repository;
    }
}