package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.StudentCourse;
import java.util.List;

@Mapper
public interface StudentCourseRepository {

    // 学生コースデータをすべて取得するSQLクエリ
    @Select("SELECT * FROM studentcourse")
    List<StudentCourse> findAllStudentCourses();
}