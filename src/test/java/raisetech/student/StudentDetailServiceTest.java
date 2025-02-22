package raisetech.student;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.repository.StudentRepository;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class StudentDetailServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentCourseService studentCourseService;

    @InjectMocks
    private StudentDetailService sut;

    /**
     * 正常系テスト：全学生詳細が正しくマッピングされるか
     */
    @Test
    void 正常系_全学生の詳細情報が正しく取得される() {
        // モックデータの作成
        Student student1 = new Student(1L, "田中太郎", "タナカ", null, "tanaka@example.com", "東京都", 25, "男性", null, true, null);
        Student student2 = new Student(2L, "佐藤花子", "サトウ", null, "satou@example.com", "大阪府", 30, "女性", null, false, null);

        List<StudentCourse> coursesForTanaka = List.of(new StudentCourse(1L, 1L, "Java", null, null));
        List<StudentCourse> coursesForSatou = List.of(new StudentCourse(2L, 2L, "PHP", null, null));

        // 正しい期待値を作成
        StudentDetail expectedDetail1 = new StudentDetail(student1, coursesForTanaka);
        StudentDetail expectedDetail2 = new StudentDetail(student2, coursesForSatou);

        // モックの設定
        Mockito.when(studentRepository.findAllStudents())
                .thenReturn(List.of(student1, student2));
        Mockito.when(studentCourseService.findByStudentId(1L))
                .thenReturn(coursesForTanaka);
        Mockito.when(studentCourseService.findByStudentId(2L))
                .thenReturn(coursesForSatou);

        // テスト対象のメソッドを呼び出し
        List<StudentDetail> actualResult = sut.findAllStudentDetails();

        // 比較対象の出力確認（デバッグ用）
        System.out.println("Actual Result: " + actualResult);

        // 再帰的比較を用いて正しい結果を検証
        Assertions.assertThat(actualResult)
                .usingRecursiveComparison()
                .isEqualTo(List.of(expectedDetail1, expectedDetail2));
    }

    /**
     * 異常系テスト：学生データが空の場合
     */
    @Test
    void 異常系_学生データが存在しない場合() {
        // モックデータを空に
        Mockito.when(studentRepository.findAllStudents()).thenReturn(List.of());

        // テスト実行
        List<StudentDetail> result = sut.findAllStudentDetails();

        // 空リストが返されることを検証
        Assertions.assertThat(result).isEmpty();

        // リポジトリが正しく呼ばれたか検証
        Mockito.verify(studentRepository, Mockito.times(1)).findAllStudents();
        Mockito.verifyNoInteractions(studentCourseService); // CourseServiceは呼び出されない
    }

    /**
     * 異常系テスト：コース情報が存在しない場合
     */
    @Test
    void 異常系_特定学生にコース情報が存在しない() {
        // モックデータの作成
        Student student = new Student(1L, "田中太郎", "タナカタロウ", null, "tanaka@example.com", "東京都", 25, "男性", null, true, null);
        // モックの振る舞い
        Mockito.when(studentRepository.findAllStudents()).thenReturn(List.of(student));
        Mockito.when(studentCourseService.findByStudentId(1L)).thenReturn(List.of()); // 空のリストを返す

        // テスト実行
        List<StudentDetail> actualResult = sut.findAllStudentDetails();

        // 期待値作成
        List<StudentDetail> expectedDetails = List.of(new StudentDetail(student, List.of()));

        // アサーション
        Assertions.assertThat(actualResult).usingRecursiveComparison().isEqualTo(expectedDetails);
    }

    /**
     * 異常系テスト：依存コンポーネントが例外をスローした場合
     */
    @Test
    void 異常系_リポジトリが例外をスローする場合() {
        // モックの振る舞い
        Mockito.when(studentRepository.findAllStudents()).thenThrow(new RuntimeException("Database error"));

        // 実行時例外のスローを期待
        Assertions.assertThatThrownBy(() -> sut.findAllStudentDetails())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        // リポジトリが呼ばれたことを確認
        Mockito.verify(studentRepository, Mockito.times(1)).findAllStudents();
    }
}
