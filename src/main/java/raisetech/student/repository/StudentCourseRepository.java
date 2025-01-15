package raisetech.student.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import raisetech.student.data.StudentCourse;

import java.util.List;

@Mapper
public interface StudentCourseRepository {

    @Select("SELECT * FROM student_courses WHERE student_id = #{studentId}")
    List<StudentCourse> findByStudentId(@Param("studentId") Long studentId);

    @Insert("INSERT INTO student_courses (course_name, course_start_at, course_end_at, student_id) " +
            "VALUES (#{courseName}, #{courseStartAt}, #{courseEndAt}, #{studentId})")
    int insertCourse(StudentCourse course);

    @Update("UPDATE student_courses SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} WHERE id = #{id}")
    int updateCourse(StudentCourse course);

    @Update("UPDATE student_courses " +
            "SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} " +
            "WHERE student_id = #{studentId}")
    void updateCoursesByStudentId(@Param("studentId") Long studentId,
                                  @Param("courseName") String courseName,
                                  @Param("courseStartAt") String courseStartAt,
                                  @Param("courseEndAt") String courseEndAt);

    @Delete("DELETE FROM student_courses WHERE id = #{id}")
    int deleteCourse(@Param("id") Long id);
}