package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.StudentCourses;
import java.util.List;

@Mapper
public interface StudentCourseRepository {
    @Select("SELECT * FROM studentscourses WHERE studentscourses.student_id = #{studentId}")
    List<StudentCourses> findCoursesByStudentId(@Param("studentId") int studentId);
}