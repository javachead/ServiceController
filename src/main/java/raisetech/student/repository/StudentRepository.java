package raisetech.student.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import raisetech.student.data.Student;

@Mapper
public interface StudentRepository {

    /**
     * IDで学生情報を取得。
     * @param id 学生のID
     * @return 学生情報（Optionalでラップ）
     */
    Optional<Student> findById(Long id);

    /**
     * 全ての学生情報を取得。
     * @return 学生情報のリスト
     */
    List<Student> findAllStudents();

    /**
     * 学生情報を追加。
     * @param student 登録する学生オブジェクト
     */
    void insertStudent(Student student);

    /**
     * 学生情報を更新。
     * @param student 更新対象の学生データオブジェクト
     */
    void updateStudentDetails(Student student);

    /**
     * 学生情報を削除。
     * @param id 削除する学生のID
     */
    void deleteById(Long id);
}