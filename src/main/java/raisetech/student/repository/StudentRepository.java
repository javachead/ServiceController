package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import raisetech.student.data.Student;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentRepository {

    /**
     * IDで学生情報を取得。
     *
     * @param id 学生のID
     * @return 学生情報（Optionalでラップ）
     */
    Optional<Student> findById(Long id);

    /**
     * 全ての学生情報を取得。
     *
     * @return 学生情報のリスト
     */
    List<Student> findAllStudents();

    /**
     * 学生情報の新規登録または更新。
     *
     * @param student 対象の学生データオブジェクト
     */
    Student save(Student student);

    /**
     * ID が一致する学生情報を削除。
     *
     * @param id 削除する学生の ID
     */
    void deleteById(Long id);
}
