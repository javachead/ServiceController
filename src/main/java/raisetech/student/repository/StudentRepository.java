package raisetech.student.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import raisetech.student.data.Student;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentRepository {

    @Select("SELECT * FROM student WHERE id = #{id}")
    Optional<Student> findById(Long id);

    @Select("SELECT * FROM student")
    List<Student> findAllStudents();

    @Insert("INSERT INTO student (name, kana_name, nickname, email, area, age, sex, remark) " +
            "VALUES (#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStudent(Student student);

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
    int updateStudentDetails(Student student);

    @Delete("DELETE FROM student WHERE id = #{id}")
    int deleteById(Long id); // 戻り値をintに変更
}