package raisetech.student.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.data.Student;

@Mapper
public interface StudentRepository {

    // --------------------- IDで学生情報を取得 ---------------------

    /**
     * 指定されたIDの学生情報を取得。
     * @param id 学生のID
     * @return 学生情報(Optionalでラップ)
     * 注意: 存在しない場合は空の`Optional`が返るため、nullチェックは不要。
     */
    @Select("SELECT * FROM student WHERE id = #{id}")
    Optional<Student> findById(Long id);

    /**
     * 全学生情報を取得。
     * 現在の設計では、論理削除された学生も返却される可能性があるため、必要に応じて条件追加を要検討。
     * @return 学生情報のリスト
     */
    @Select("SELECT * FROM student")
    List<Student> findAllStudents();

    // --------------------- 学生情報を追加 ---------------------

    /**
     * 新しい学生情報を登録。
     * @param student 登録する学生オブジェクト
     * 注意: 自動生成されたIDが、引数の`student`オブジェクト内に設定される。
     */
    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark, is_deleted) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStudent(Student student);

    // --------------------- 学生情報を更新 ---------------------

    /**
     * 指定された学生情報を更新する。全てのプロパティを対象とし、部分的な更新には未対応。
     * @param student 更新対象の学生データオブジェクト
     * 注意: `is_deleted` フィールドも更新されるため、削除マークを誤って変更しないように十分注意。
     */
    @Update("UPDATE student SET " +
            "name = #{name}, " +
            "kana_name = #{kanaName}, " +
            "nickname = #{nickname}, " +
            "email = #{email}, " +
            "area = #{area}, " +
            "age = #{age}, " +
            "sex = #{sex}, " +
            "remark = #{remark}, " +
            "is_deleted = #{deleted} " +
            "WHERE id = #{id}")
    void updateStudentDetails(Student student);

    // --------------------- 削除 ---------------------

    /**
     * 指定されたIDの学生情報を削除（物理削除）。
     * 注意: データベースから完全に削除されるため、通常は論理削除（is_deleted）を推奨。
     * @param id 削除する学生のID
     */
    @Delete("DELETE FROM student WHERE id = #{id}")
    void deleteById(Long id);
}