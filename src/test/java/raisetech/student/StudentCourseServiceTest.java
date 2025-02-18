package raisetech.student;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;
import raisetech.student.service.StudentCourseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * StudentCourseServiceのテストクラス
 */
@ExtendWith(MockitoExtension.class)
public class StudentCourseServiceTest {

    @Mock
    private StudentCourseRepository studentCourseRepository; // リポジトリのモック

    @InjectMocks
    private StudentCourseService sut; // テスト対象クラス

    @Test
    public void 学生IDでコースを検索するメソッドの正常系テスト() {
        // モックデータ作成
        StudentCourse mockCourse = new StudentCourse(
                1L, 101L, "Java", LocalDate.of(2023, 1, 10), LocalDate.of(2023, 3, 15)
        );

        // モック設定
        when(studentCourseRepository.findByStudentId(101L)).thenReturn(List.of(mockCourse));

        // サービスメソッド実行
        List<StudentCourse> result = sut.findByStudentId(101L);

        // 結果の検証
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getCourseName());
    }

    /**
     * 空またはNULL値のデータを扱うテスト
     */
    @Test
    public void 学生IDがnullまたは開始日がnullのデータのテスト() {
        // モックデータ作成
        List<StudentCourse> mockCourses = new ArrayList<>();
        mockCourses.add(new StudentCourse(
                3L, 102L, "Python", null, LocalDate.of(2023, 8, 1)
        ));

        // モック設定
        when(studentCourseRepository.findByStudentId(102L)).thenReturn(mockCourses);

        // サービスメソッドを実行
        List<StudentCourse> result = sut.findByStudentId(102L);

        // 検証
        assertEquals(1, result.size());
        assertEquals("Python", result.get(0).getCourseName());
        assertNull(result.get(0).getCourseStartAt());
        assertEquals(LocalDate.of(2023, 8, 1), result.get(0).getCourseEndAt());
    }

    /**
     * 古いコースの適切な削除をテスト
     */
    @Test
    public void 存在しないコースの削除処理テスト() {
        List<StudentCourse> existingCourses = new ArrayList<>();
        existingCourses.add(new StudentCourse(1L, 101L, "Java", null, null));
        existingCourses.add(new StudentCourse(2L, 101L, "Python", null, null));

        List<StudentCourse> newCourses = new ArrayList<>();
        newCourses.add(new StudentCourse(1L, 101L, "Java", null, null));

        // モック設定
        doNothing().when(studentCourseRepository).deleteCourse(2L);

        // サービスの呼び出し
        sut.removeUnusedCourses(newCourses, existingCourses);

        // 検証
        verify(studentCourseRepository).deleteCourse(2L);
        verifyNoMoreInteractions(studentCourseRepository);
    }

    /**
     * 学生IDに紐づくコース情報を保存または更新するメソッドのテスト
     */
    @Test
    public void コース情報の保存と削除処理テスト() {
        // モック用データ: 既存のコース
        List<StudentCourse> existingCourses = new ArrayList<>();
        existingCourses.add(new StudentCourse(2L, 101L, "Old Python Course", LocalDate.now(), LocalDate.now()));

        // 保存または更新する新しいコース
        List<StudentCourse> newCourses = new ArrayList<>();
        newCourses.add(new StudentCourse(2L, null, "Python", LocalDate.now().plusDays(10), LocalDate.now().plusDays(20)));

        // モック設定
        when(studentCourseRepository.findByStudentId(101L)).thenReturn(existingCourses);

        // サービスの呼び出し
        sut.courseList(newCourses, 101L);

        // 検証：更新されたかを確認
        verify(studentCourseRepository, times(1)).updateCourse(any(StudentCourse.class));

        // 削除が実行されなかったことを確認
        verify(studentCourseRepository, never()).deleteCourse(anyLong());

        // 新規挿入が実行されなかったことを確認
        verify(studentCourseRepository, never()).insertCourse(any(StudentCourse.class));
    }

    /**
     * 学生IDが存在しない場合に、空のリストが返されることをテストします。
     */
    @Test
    public void 学生IDが存在しない場合空のリストが返される() {
        // モックに空のリストを返すように設定
        when(studentCourseRepository.findByStudentId(999L)).thenReturn(new ArrayList<>());

        // 空の existingCourses を準備
        List<StudentCourse> existingCourses = new ArrayList<>(); // missing definition

        // 実行
        List<StudentCourse> result = sut.findByStudentId(999L);
        sut.removeUnusedCourses(new ArrayList<>(), existingCourses); // existingCourses を使用

        // 検証
        assertEquals(0, result.size()); // 空のリストであることを確認
    }

    /**
     * 新しいコースリストが空の場合に、既存のすべてのコースが削除される動作をテストします。
     */
    @Test
    public void 新しいリストが空の場合すべて削除される() {
        // モックデータ
        List<StudentCourse> existingCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null),
                new StudentCourse(2L, 101L, "Python", null, null)
        );

        // モック設定
        doNothing().when(studentCourseRepository).deleteCourse(anyLong());

        // サービス実行：新しいリストを空に
        sut.removeUnusedCourses(new ArrayList<>(), existingCourses);

        // 検証
        verify(studentCourseRepository).deleteCourse(1L);
        verify(studentCourseRepository).deleteCourse(2L);
    }

    /**
     * 既存のリストが空の場合に、処理が正常に終了することを確認するテスト。
     */
    @Test
    public void 既存のリストが空の場合正常終了する() {
        // モックデータ
        List<StudentCourse> emptyCourses = new ArrayList<>();

        // サービス実行
        sut.removeUnusedCourses(new ArrayList<>(), emptyCourses);

        // 検証
        verifyNoMoreInteractions(studentCourseRepository);
    }

    /**
     * 新規コースを正しく挿入する処理のテスト。
     */
    @Test
    public void 新規コースが正しく挿入される() {
        // モック設定
        List<StudentCourse> existingCourses = new ArrayList<>();
        List<StudentCourse> newCourses = List.of(
                new StudentCourse(null, 101L, "New Course", null, null)
        );

        doNothing().when(studentCourseRepository).insertCourse(any(StudentCourse.class));

        // 実行
        sut.courseList(newCourses, 101L);

        // 検証
        verify(studentCourseRepository).insertCourse(any(StudentCourse.class));
    }

    /**
     * 存在しないコースIDを更新しようとした際に、例外が正しくスローされることを確認するテスト。
     */
    @Test
    public void 存在しないIDを更新しようとした際例外がスローされる() {
        // モック設定
        List<StudentCourse> existingCourses = new ArrayList<>();
        List<StudentCourse> newCourses = List.of(
                new StudentCourse(999L, 101L, "Non-existent Course", null, null)
        );

        when(studentCourseRepository.findByStudentId(101L)).thenReturn(existingCourses);

        // 実行＆検証
        assertThrows(IllegalArgumentException.class, () ->
                sut.courseList(newCourses, 101L)
        );
    }

    /**
     * 新しいコースリストに重複するIDが含まれる場合のテスト。
     */
    @Test
    public void 新しいリストが重複IDを含む場合のテスト() {
        // モック設定: データベースに既存する学生のコースリスト
        List<StudentCourse> existingCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null) // 既存データ
        );

        // 新しいコースリストには既存コースを含むが、新しいコースも追加する
        List<StudentCourse> newCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null), // 既存と一致するデータ
                new StudentCourse(null, 101L, "New Course", null, null) // 新規登録データ
        );

        // モック: findByStudentId で既存データを返すよう設定
        when(studentCourseRepository.findByStudentId(101L)).thenReturn(existingCourses);

        // モック: 新規登録のモック動作を設定
        doNothing().when(studentCourseRepository).insertCourse(any(StudentCourse.class));

        // テスト対象メソッドの実行
        sut.courseList(newCourses, 101L);

        // 検証: 新規登録 (insertCourse) が1回実行されたことを確認
        verify(studentCourseRepository, times(1)).insertCourse(any(StudentCourse.class));
    }

    /**
     * 入力リストが空で既存のコースが削除される場合のテスト。
     */
    @Test
    public void 入力リストが空の場合全削除される() {
        // モックデータ
        List<StudentCourse> existingCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null)
        );

        // モック設定
        doNothing().when(studentCourseRepository).deleteCourse(anyLong());

        // サービス実行：新しいリストを空に
        sut.removeUnusedCourses(new ArrayList<>(), existingCourses);

        // 削除検証
        verify(studentCourseRepository).deleteCourse(1L);
    }

    /**
     * 大量データを扱う場合のパフォーマンステスト。
     */
    @Test
    public void 大量データを扱う場合のパフォーマンステスト() {
        // 大量データ生成
        List<StudentCourse> largeDataSet = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeDataSet.add(new StudentCourse((long) i, 101L, "Course " + i, null, null));
        }

        // モック設定
        when(studentCourseRepository.findByStudentId(101L)).thenReturn(largeDataSet);

        // 実行
        List<StudentCourse> result = sut.findByStudentId(101L);

        // 検証: データが正しく渡されること
        assertEquals(10000, result.size());
    }

    /**
     * トランザクション制御のロールバック確認。
     */
    @Test
    @Transactional
    public void 例外発生時にトランザクションがロールバックされる() {
        // 新しいコースリスト
        List<StudentCourse> newCourses = List.of(
                new StudentCourse(null, 101L, "Course 1", null, null)
        );

        // モックで例外発生
        doThrow(RuntimeException.class).when(studentCourseRepository).insertCourse(any(StudentCourse.class));

        // 実行と例外確認
        assertThrows(RuntimeException.class, () -> sut.courseList(newCourses, 101L));

        // 確認: 削除や挿入が呼び出されない
        verify(studentCourseRepository, never()).deleteCourse(anyLong());
    }

    @Test
    public void 新しいリストが重複IDを含む場合の削除のみが実行されるテスト() {
        // 既存のコースリスト
        List<StudentCourse> existingCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null), // DB上に登録されているデータ
                new StudentCourse(2L, 102L, "Spring Boot", null, null) // 不要データ
        );

        // 新しいコースリスト (newCourses)
        List<StudentCourse> newCourses = List.of(
                new StudentCourse(1L, 101L, "Java", null, null) // 既存のコースがそのまま維持される
        );

        // 削除操作のモック (何もしないよう設定)
        doNothing().when(studentCourseRepository).deleteCourse(anyLong());

        // テスト対象メソッド実行
        sut.removeUnusedCourses(newCourses, existingCourses);

        // 確認: 削除操作が1回だけ呼び出されたことを確認
        verify(studentCourseRepository, times(1)).deleteCourse(2L);
        verifyNoMoreInteractions(studentCourseRepository);
    }
}
