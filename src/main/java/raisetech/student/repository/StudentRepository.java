package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import raisetech.student.data.Student;
import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentRepository {

    // --------------------- IDで学生情報を取得 ---------------------

    @Select("SELECT * FROM student WHERE id = #{id}")
    Optional<Student> findById(Long id);

    @Select("SELECT * FROM student")
    List<Student> findAllStudents();

    // --------------------- 学生情報を追加 ---------------------

    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark, is_deleted) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStudent(Student student);

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

    @Delete("DELETE FROM student WHERE id = #{id}")
    void deleteById(Long id);
}