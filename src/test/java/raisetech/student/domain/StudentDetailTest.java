package raisetech.student.domain;

import org.junit.jupiter.api.Test;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentDetailTest {

    /*デフォルトコンストラクタ呼び出し時に、studentCourses が必ず空のリストとして初期化されることを確認*/
    @Test
    void デフォルトコンストラクタはstudentCoursesを空のリストとして初期化する() {
        // デフォルトコンストラクタを使用してオブジェクトを初期化
        StudentDetail actual = new StudentDetail();

        // studentCourses の初期状態を確認
        assertThat(actual.getStudentCourses())
                .isNotNull()           // nullではないこと
                .isInstanceOf(List.class) // リストであること
                .isEmpty();            // 空であること
    }


    /*StudentDetail クラスの equals メソッドが正しく動作するかを検証する*/
    @Test
    void オブジェクト内容を全体で比較する() {
        // 実際のオブジェクト
        StudentDetail actual = new StudentDetail();
        actual.setStudent(null); // 初期状態
        actual.setStudentCourses(Collections.emptyList());

        // 不正なデータでエラーを確かめる例
        StudentDetail expected = new StudentDetail();
        expected.setStudent(new Student()); // 違う値を設定
        expected.setStudentCourses(Collections.singletonList(new StudentCourse()));

        // 期待される結果と異なることを確認（クラスの責務を検証する）
        assertThat(actual).isNotEqualTo(expected);
    }
}
