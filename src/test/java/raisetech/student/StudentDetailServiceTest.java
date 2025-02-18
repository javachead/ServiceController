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
        Student student2 = new Student(2L, "佐藤花子", "サトウ", null, "Satou@example.com", "大阪府", 30, "女性", null, false, null);

        List<Student> students = List.of(student1, student2);

        List<StudentCourse> courseFortanaka = List.of(new StudentCourse(1L, 1L, "Java", null, null));
        List<StudentCourse> coursesForsatou = List.of(new StudentCourse(2L, 2L, "PHP", null, null));

        // モックの振る舞いを定義
        Mockito.when(studentRepository.findAllStudents()).thenReturn(students);
        Mockito.when(studentCourseService.findByStudentId(1L)).thenReturn(courseFortanaka);
        Mockito.when(studentCourseService.findByStudentId(2L)).thenReturn(coursesForsatou);

        // テスト実行
        List<StudentDetail> result = sut.findAllStudentDetails();

        // 結果の検証
        Assertions.assertThat(result).hasSize(2);

        // 検証：1人目の学生データ
        Assertions.assertThat(result.getFirst().getStudent().getName()).isEqualTo("田中太郎");
        Assertions.assertThat(result.get(0).getStudentCourses()).hasSize(1);
        Assertions.assertThat(result.get(0).getStudentCourses().getFirst().getCourseName()).isEqualTo("Java");

        // 検証：2人目の学生データ
        Assertions.assertThat(result.get(1).getStudent().getName()).isEqualTo("佐藤花子");
        Assertions.assertThat(result.get(1).getStudentCourses()).hasSize(1);
        Assertions.assertThat(result.get(1).getStudentCourses().getFirst().getCourseName()).isEqualTo("PHP");

        // リポジトリとサービスの呼び出し回数の検証
        Mockito.verify(studentRepository, Mockito.times(1)).findAllStudents();
        Mockito.verify(studentCourseService, Mockito.times(1)).findByStudentId(1L);
        Mockito.verify(studentCourseService, Mockito.times(1)).findByStudentId(2L); // IDに注意
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
        List<StudentDetail> result = sut.findAllStudentDetails();

        // 検証
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getFirst().getStudent().getName()).isEqualTo("田中太郎");
        Assertions.assertThat(result.getFirst().getStudentCourses()).isEmpty(); // コース情報が空

        // 呼び出し確認
        Mockito.verify(studentRepository, Mockito.times(1)).findAllStudents();
        Mockito.verify(studentCourseService, Mockito.times(1)).findByStudentId(1L);
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
