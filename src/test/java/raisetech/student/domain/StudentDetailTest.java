package raisetech.student.domain;

import org.junit.jupiter.api.Test;

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

    /*StudentDetail クラスの toString が 初期化されたフィールド内容を正しく反映しているかを確認*/
    @Test
    void オブジェクト内容を全体で比較する() {
        // 実際のオブジェクト
        StudentDetail actual = new StudentDetail();
        actual.setStudent(null); // 必要に応じて値を設定
        actual.setStudentCourses(Collections.emptyList()); // 空リストを設定

        // 期待されるオブジェクトの準備
        StudentDetail expected = new StudentDetail();
        expected.setStudent(null);
        expected.setStudentCourses(Collections.emptyList());

        // オブジェクト全体を再帰的に比較して一致していることを確認
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
