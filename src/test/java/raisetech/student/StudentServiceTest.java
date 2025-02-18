package raisetech.student;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.data.Student;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.repository.StudentRepository;
import raisetech.student.service.StudentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * StudentServiceクラスに対する単体テストのクラス。
 * 各メソッドの正常系・異常系テストケース
 */

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService sut;

    @Test
    void 学生情報を正しく取得できるかのテスト_正常系() {
        // モックデータの準備
        Long id = 1L;
        Student student = new Student(
                id,
                "山田太郎",
                "ヤマダタロウ",
                "taro123",
                "taro@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );

        Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student)); // モックの振る舞いを定義

        // テスト実行
        Student result = sut.getStudentById(1L); // sut(StudentService)を使用

        // 検証
        Assertions.assertEquals("taro@example.com", result.getEmail()); // 期待値と一致
        Assertions.assertEquals("山田太郎", result.getName());
    }

    @Test
    void 存在しないIDの場合_例外がスローされるかのテスト_異常系() {
        // 存在しないIDを検索するようにモックを設定
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        // メソッド実行時に例外が発生することを確認
        assertThrows(StudentNotFoundException.class, () -> sut.getStudentById(id));
        // 検証：findByIdが1回呼び出されているか確認
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void 存在する学生を削除できるかのテスト_正常系() {
        // 削除対象の学生データを準備
        Long id = 1L;
        Student student = new Student(
                id,
                "山田太郎",
                "ヤマダタロウ",
                "taro123",
                "updated@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).deleteById(id);

        // メソッド実行
        sut.deleteStudentById(id);

        // 検証：findByIdとdeleteByIdがそれぞれ1回呼び出されているか確認
        verify(studentRepository, times(1)).findById(id);
        verify(studentRepository, times(1)).deleteById(id);
    }

    @Test
    void 存在しない学生を削除しようとした場合に例外がスローされるかのテスト_異常系() {
        // 存在しないIDを検索するようにモックを設定
        Long id = 1L;
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        // メソッド実行時に例外が発生することを確認
        assertThrows(StudentNotFoundException.class, () -> sut.deleteStudentById(id));
        // 検証：findByIdは1回呼ばれ、deleteByIdは一度も呼ばれないことを確認
        verify(studentRepository, times(1)).findById(id);
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void IDがnullや新規作成の場合学生情報を保存できるかのテスト_正常系() {
        // 新規学生データを準備
        Long id = null;
        Student student = new Student(
                id,
                "山田太郎",
                "ヤマダタロウ",
                "taro123",
                "updated@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );

        // モックで保存時にIDがセットされるように設定
        when(studentRepository.save(student)).thenAnswer(invocation -> {
            Student argument = invocation.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        // メソッド実行
        Student result = sut.save(id, student);

        // 検証：saveが1回呼び出されているか確認
        verify(studentRepository, times(1)).save(student);
        // IDが自動セットされているか確認
        assertEquals(1L, result.getId());
        assertEquals("山田太郎", result.getName());
    }

    @Test
    void IDが指定されている学生情報を更新できるかのテスト_正常系() {
        // 更新対象の学生データを準備
        Long id = 1L;
        Student existingStudent = new Student(
                id,
                "山田太郎", // 既存の名前
                "ヤマダタロウ",
                "taro123",
                "taro@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );

        // 更新後の学生エンティティを準備
        Student updatedStudent = new Student(
                id,
                "更新太郎",
                "コウシンタロウ",
                "koushin123",
                "koushin123@example.com",
                "大阪府",
                21,
                "男性",
                "更新された備考",
                false,
                null
        );

        // モックリポジトリの設定: 既存の学生情報を返す
        Mockito.when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

        // モックリポジトリの設定: 更新を模倣
        Mockito.when(studentRepository.save(Mockito.any(Student.class))).thenReturn(updatedStudent);

        // テスト実行
        Student result = sut.save(id, updatedStudent); // サービスの更新メソッドを呼び出し

        // 更新処理後の検証
        Assertions.assertEquals("更新太郎", result.getName()); // 名前が更新されたか確認
        Assertions.assertEquals("koushin123@example.com", result.getEmail()); // Emailが更新されたか確認
        Assertions.assertEquals("大阪府", result.getArea()); // Areaが更新されたか確認
        Assertions.assertEquals(21, result.getAge()); // Ageが数値型で更新されたか確認
        Assertions.assertEquals("更新された備考", result.getRemark()); // 備考が更新されたか確認
    }

    @Test
    void 更新対象の学生が存在しない場合例外がスローされるかのテスト_異常系() {
        // 更新対象の学生データを準備
        Long id = 1L;
        Student student = new Student(
                id,
                "山田太郎",
                "ヤマダタロウ",
                "taro123",
                "updated@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );

        // 存在しない学生データをモックとして設定
        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        // メソッド実行時に例外が発生することを確認
        assertThrows(StudentNotFoundException.class, () -> sut.save(id, student));
        // 検証：findByIdが1回呼び出され、saveが呼び出されていないことを確認
        verify(studentRepository, times(1)).findById(id);
        verify(studentRepository, never()).save(any(Student.class));
    }
}
