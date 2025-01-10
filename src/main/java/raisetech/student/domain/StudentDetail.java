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
    private Student student;

    @Valid
    private List<StudentCourse> studentCourses; //学生が登録しているコース情報

    public StudentDetail() {
        this.studentCourses = new ArrayList<>(); // 初期化
    }

    public StudentDetail(Student student, List<StudentCourse> studentCourses) {
        this.student = student;
        this.studentCourses = studentCourses != null ? studentCourses : new ArrayList<>();
    }

    // course情報を追加するメソッド
    public void addCourse(StudentCourse course) {
        if (this.studentCourses == null) {
            this.studentCourses = new ArrayList<>();
        }
        this.studentCourses.add(course);
    }
}