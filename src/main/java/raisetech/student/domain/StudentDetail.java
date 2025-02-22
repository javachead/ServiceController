package raisetech.student.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生詳細情報のドメインクラス。
 * 学生情報 (Student) と紐付くコース情報 (StudentCourse) を管理します。
 * バリデーションは @Valid による Bean Validation を使用。
 */
@Schema(description = "受講生詳細情報 - 学生情報と登録されているコース情報のセット")
@Setter
@Getter
@EqualsAndHashCode
@ToString

public class StudentDetail {

    @Valid
    @Schema(description = "学生情報", implementation = Student.class)
    private Student student; // 学生情報を保持

    @Valid
    @ArraySchema(
            schema = @Schema(description = "登録済みのコース情報", implementation = StudentCourse.class)
    )
    @JsonIgnore // student内のデータと重複しないように除外
    private List<StudentCourse> studentCourses; // 学生が登録しているコース情報のリスト

    /**
     * デフォルトコンストラクタ。
     * 初期状態でコースリストを空のリストとして初期化します。
     * null による NullPointerException を防ぐ意図があります。
     */
    public StudentDetail() {
        this.studentCourses = new ArrayList<>();
    }

    /**
     * 学生情報とコース情報を設定するコンストラクタ。
     *
     * @param student        学生情報
     * @param studentCourses 学生が登録しているコース情報のリスト
     */
    public StudentDetail(Student student, List<StudentCourse> studentCourses) {
        this.student = student;  // 学生情報をセット
        // コース情報はnullチェックを行い、nullであれば空リストで初期化
        this.studentCourses = studentCourses != null ? new ArrayList<>(studentCourses) : new ArrayList<>();
    }
}
