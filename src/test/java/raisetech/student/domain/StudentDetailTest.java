package raisetech.student.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StudentDetailTest {

    @Test
    void 正常系_デフォルトコンストラクタで初期化されることを確認する() {
        StudentDetail actual = new StudentDetail(); // 実行
        StudentDetail expected = new StudentDetail(null, new ArrayList<>()); // 期待値

        // オブジェクト全体の比較
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 正常系_引数付きコンストラクタで正しく設定されることを確認する() {
        Student testStudent = new Student(1L, "太郎", "タロウ", "user123", "taro@example.com",
                "東京都", 20, "男性", "備考", false, null);
        List<StudentCourse> testCourses = List.of(
                new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)),
                new StudentCourse(1L, 102L, "コース2", LocalDate.of(2023, 2, 1), LocalDate.of(2023, 5, 25))
        );

        StudentDetail actual = new StudentDetail(testStudent, testCourses); // 実行
        StudentDetail expected = new StudentDetail(testStudent, testCourses); // 期待値

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected); // 比較
    }

    @Test
    void 正常系_studentCoursesがnullの場合デフォルトで空リストに初期化されることを確認する() {
        Student testStudent = new Student(1L, "太郎", "タロウ", "user123", "taro@example.com",
                "東京都", 20, "男性", "備考", false, null);

        StudentDetail actual = new StudentDetail(testStudent, null); // 実行
        StudentDetail expected = new StudentDetail(testStudent, new ArrayList<>()); // 期待値

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected); // 比較
    }

    @Test
    void 正常系_引数リストが空の場合正しく設定されることを確認する() {
        // テスト用データ
        Student testStudent = new Student(1L, "太郎", "タロウ", "user123", "taro@example.com", "東京都", 20, "男性", "備考", false, null);

        // 実行
        StudentDetail actual = new StudentDetail(testStudent, List.of());

        // 期待値
        StudentDetail expected = new StudentDetail(testStudent, List.of());

        // 検証
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 正常系_studentCoursesのリストを操作しても影響しないことを確認する() {
        // テスト用データ
        Student testStudent = new Student(1L, "太郎", "タロウ", "user123", "taro@example.com", "東京都", 20, "男性", "備考", false, null);
        List<StudentCourse> originalList = new ArrayList<>();
        originalList.add(new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)));

        // 実行
        StudentDetail actual = new StudentDetail(testStudent, originalList);
        originalList.add(new StudentCourse(1L, 102L, "コース2", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)));

        // 期待値
        StudentDetail expected = new StudentDetail(testStudent, List.of(
                new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15))
        ));
        // 検証
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 正常系_JSONをオブジェクトにデシリアライズできるか確認する() throws Exception {
        // テスト用JSON
        String jsonInput = "{ \"student\": { \"id\": 1, \"name\": \"確認太郎\" }, \"studentCourses\": [] }";

        // 実行
        ObjectMapper objectMapper = new ObjectMapper();
        StudentDetail actual = objectMapper.readValue(jsonInput, StudentDetail.class);

        // 期待値
        Student expected = new Student(1L, "確認太郎", null, null, null, null, null, null, null, null, null);
        StudentDetail expectedDetail = new StudentDetail(expected, new ArrayList<>());

        // 検証
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedDetail);
    }

    @Test
    void 正常系_equalsAndHashCode_リストの順序に関係なく等価判定を行う() {
        List<StudentCourse> courses1 = List.of(
                new StudentCourse(1L, 101L, "コースA", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)),
                new StudentCourse(2L, 101L, "コースB", LocalDate.of(2023, 4, 10), LocalDate.of(2023, 7, 20))
        );
        List<StudentCourse> courses2 = List.of(
                new StudentCourse(2L, 101L, "コースB", LocalDate.of(2023, 4, 10), LocalDate.of(2023, 7, 20)),
                new StudentCourse(1L, 101L, "コースA", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15))
        );
        Student student = new Student(1L, "山田太郎", "ヤマダタロウ", "taro123",
                "taro@example.com", "東京都", 20, "男性", "備考", false, null);

        // オブジェクトの作成
        StudentDetail detail1 = new StudentDetail(student, courses1);
        StudentDetail detail2 = new StudentDetail(student, courses2);

        // リスト順序を無視して等価比較
        assertThat(detail1).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(detail2);
    }

    @Test
    void 正常系_equalsAndHashCode_リストの順序が異なる場合等価でないと判定される() {
        // テストデータ
        List<StudentCourse> orderedCourses1 = List.of(
                new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15))
        );
        List<StudentCourse> orderedCourses2 = List.of(
                new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)),
                new StudentCourse(2L, 102L, "コース2", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 15))
        );
        Student student = new Student(1L, "山田太郎", "ヤマダタロウ", "taro123", "taro@example.com", "東京都", 20, "男性", "備考", false, null);

        // 実行
        StudentDetail detail1 = new StudentDetail(student, orderedCourses1);
        StudentDetail detail2 = new StudentDetail(student, orderedCourses2);

        // 検証
        assertThat(detail1).isNotEqualTo(detail2);
    }

    @Test
    void 正常系_hashCode_等価なオブジェクトは同一ハッシュコードになる() {
        Student student = new Student(1L, "山田太郎", "ヤマダタロウ", "taro123",
                "taro@example.com", "東京都", 20, "男性", "備考", false, null);
        List<StudentCourse> courses = List.of(
                new StudentCourse(1L, 101L, "コース1", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)),
                new StudentCourse(2L, 102L, "コース2", LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 15))
        );

        StudentDetail detail1 = new StudentDetail(student, courses);
        StudentDetail detail2 = new StudentDetail(student, courses);

        // 等価なオブジェクト同士を比較
        assertThat(detail1).isEqualTo(detail2);
        assertThat(detail1.hashCode()).isEqualTo(detail2.hashCode()); // ハッシュコードも一致することを確認
    }

    @Test
    void 異常系_studentCoursesがnullでstudentもnullの場合初期化が正しいか確認する() {
        // 実行
        StudentDetail actual = new StudentDetail(null, null);

        // 期待値
        StudentDetail expected = new StudentDetail(null, new ArrayList<>());

        // 検証
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 異常系_equalsAndHashCode_異なるデータなら等価と判定されない() {
        Student student1 = new Student(1L, "山田太郎", "ヤマダタロウ", "taro123",
                "updated@example.com", "東京都", 20, "男性", "備考", false, null);
        Student student2 = new Student(2L, "別人太郎", "ベツジンタロウ", "other123",
                "other@example.com", "大阪府", 25, "男性", "別備考", false, null);

        List<StudentCourse> courses1 = List.of(new StudentCourse(1L, 101L, "コース1",
                LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15)));
        List<StudentCourse> courses2 = List.of(new StudentCourse(2L, 102L, "コース2",
                LocalDate.of(2023, 5, 1), LocalDate.of(2023, 8, 15)));

        StudentDetail detail1 = new StudentDetail(student1, courses1);
        StudentDetail detail2 = new StudentDetail(student2, courses2);

        // 等価ではないことを確認
        assertThat(detail1).isNotEqualTo(detail2);
    }

    @Test
    void 異常系_JSONフォーマットが不正な場合例外がスローされる() {
        // 不正なJSON
        String jsonInput = "{ 非JSONデータ }";

        // 実行時に例外発生を確認
        ObjectMapper objectMapper = new ObjectMapper();
        assertThatThrownBy(() -> objectMapper.readValue(jsonInput, StudentDetail.class))
                .isInstanceOf(Exception.class);
    }
}
