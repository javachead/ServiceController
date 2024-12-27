package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.StudentCourse;
import java.util.List;

@Mapper
public interface StudentCourseRepository {

    // 特定の学生IDに基づいてコースを取得する
    @Select("SELECT * FROM studentcourse")
    List<StudentCourse> findCoursesByStudentId(int studentId);
}