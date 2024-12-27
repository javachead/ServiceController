package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.Student;
import java.util.List;

@Mapper
public interface StudentRepository {

    // 学生リストを全件取得するSQLクエリ
    @Select("SELECT * FROM student")
    List<Student> findAllStudents();
}