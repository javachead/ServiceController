package raisetech.student.repository;

import org.apache.ibatis.annotations.*;
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

    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStudent(Student student);

    // --------------------- 学生情報を更新 ---------------------

    @Update("UPDATE student SET " +
            "name = #{name}, " +
            "kana_name = #{kanaName}, " +
            "nickname = #{nickname}, " +
            "email = #{email}, " +
            "area = #{area}, " +
            "age = #{age}, " +
            "sex = #{sex}, " +
            "remark = #{remark} " +
            "WHERE id = #{id}")
    void updateStudentDetails(Student student);

    // --------------------- 削除 ---------------------

    @Delete("DELETE FROM student WHERE id = #{id}")
    void deleteById(Long id);
}