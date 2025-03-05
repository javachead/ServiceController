package raisetech.student.domain;

import org.junit.jupiter.api.Test;

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
}
