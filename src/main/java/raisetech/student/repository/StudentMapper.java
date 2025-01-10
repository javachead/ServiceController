package raisetech.student.repository;

import org.apache.ibatis.annotations.*;
import raisetech.student.data.Student;

import java.util.List;

@Mapper
public interface StudentMapper {

    // 学生1人を取得する
    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(Long id);

    // 全ての学生を取得する
    @Select("SELECT * FROM student")
    List<Student> findAll();

    // 学生を新規登録する（IDを自動生成して取得）
    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 自動生成されたIDを取得
    void insertStudent(Student student);
}