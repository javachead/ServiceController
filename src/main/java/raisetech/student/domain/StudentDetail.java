package raisetech.student.domain;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生詳細情報のドメインクラス。
 * 学生情報 (Student) と紐付くコース情報 (StudentCourse) を管理します。
 * バリデーションは @Valid による Bean Validation を使用。
 */
@Setter
@Getter
public class StudentDetail {

    @Valid
    private Student student; // 学生情報を保持

    @Valid
    private List<StudentCourse> studentCourses; // 学生が登録しているコース情報のリスト

    /**
     * デフォルトコンストラクタ。
     * 初期状態でコースリストを空のリストとして初期化します。
     * null による NullPointerException を防ぐ意図があります。
     */
    public StudentDetail() {
        this.studentCourses = new ArrayList<>();
    }
}
