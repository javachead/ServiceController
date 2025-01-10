package raisetech.student.repository;

import org.apache.ibatis.annotations.*;
import raisetech.student.data.Student;
import java.util.List;

@Mapper
public interface StudentRepository {

    // 単一学生を取得
    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(@Param("id") Long id);

    // 全学生を取得
    @Select("SELECT * FROM student")
    List<Student> findAllStudents();

    // 新規学生登録
    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertStudent(Student student);
}