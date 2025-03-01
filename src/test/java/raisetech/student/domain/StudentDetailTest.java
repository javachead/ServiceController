package raisetech.student.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class StudentDetailTest {

    @Test
    void 正常系_デフォルトコンストラクタで初期化されることを確認する() {
        StudentDetail actual = new StudentDetail();

        // 期待される初期状態のオブジェクトを作成
        StudentDetail expected = new StudentDetail();
        expected.setStudent(null);
        expected.setStudentCourses(new ArrayList<>());

        // オブジェクト全体を比較
        assertThat(actual).isEqualTo(expected);
    }
}
