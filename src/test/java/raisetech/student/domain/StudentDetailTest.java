package raisetech.student.domain;

import org.junit.jupiter.api.Test;
import raisetech.student.data.StudentCourse;

import java.time.LocalDate;
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

    /*初期化されたリストがミュータブル（追加可能）であること。
    データが正しく追加されること（個別フィールドの値も確認）*/
    @Test
    void デフォルトコンストラクタで初期化されたstudentCoursesに要素を追加できる() {
        // デフォルトコンストラクタを使用してオブジェクトを初期化
        StudentDetail actual = new StudentDetail();

        // studentCourses に要素を追加
        actual.getStudentCourses().add(new StudentCourse(
                1L, 2L, "Java",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31)
        ));

        // 要素が正しく追加されていることを確認
        assertThat(actual.getStudentCourses()).hasSize(1); // サイズが1であること
        assertThat(actual.getStudentCourses().get(0).getCourseName()).isEqualTo("Java"); // 内容を確認
    }

    /*StudentDetail クラスの toString が 初期化されたフィールド内容を正しく反映しているかを確認*/
    @Test
    void toStringメソッドが期待される形式で動作することを間接的に確認する() {
        StudentDetail detail = new StudentDetail();

        String toStringResult = detail.toString(); // 自動生成されたtoString

        // 項目内容が含まれていることを確認（柔軟に確認できます）
        assertThat(toStringResult).contains("StudentDetail"); // クラス名 "StudentDetail" を含んでいることを確認
        assertThat(toStringResult).contains("student=null"); // studentフィールドがnullを示す
        assertThat(toStringResult).contains("studentCourses=[]");// studentCoursesが空リストを示す
    }
}
