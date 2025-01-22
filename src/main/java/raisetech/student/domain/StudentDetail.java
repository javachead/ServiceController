package raisetech.student.domain;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class StudentDetail {

    @Valid
    private Student student; // 学生情報

    @Valid
    private List<StudentCourse> studentCourses; // 学生が登録しているコース情報

    // デフォルトコンストラクタ
    public StudentDetail() {
        this.studentCourses = new ArrayList<>();
    }

    // 学生情報＋コース情報付きコンストラクタ
    public StudentDetail(Student student, List<StudentCourse> studentCourses) {
        this.student = student;
        this.studentCourses = studentCourses != null ? studentCourses : new ArrayList<>();
    }

    // 学生情報のみコンストラクタ（コース情報用の初期化追加）
    public StudentDetail(Student student) {
        this.student = student;
        this.studentCourses = new ArrayList<>();
    }

    // コース情報追加メソッド
    public void addCourse(StudentCourse course) {
        if (course != null) {
            this.studentCourses.add(course);
        }
    }
}