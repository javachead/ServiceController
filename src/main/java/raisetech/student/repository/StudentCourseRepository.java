package raisetech.student.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.StudentCourse;

import java.util.List;

@Mapper
public interface StudentCourseRepository {

    // 特定の学生IDに関連するコースを取得する
    @Select("SELECT * FROM student_courses WHERE student_id = #{studentId}")
    List<StudentCourse> findCoursesByStudentId(@Param("studentId") Long studentId);

    // 新しいコースを挿入する (学生IDを含む)
    @Insert("INSERT INTO student_courses (course_name, course_start_at, course_end_at, student_id) " +
            "VALUES (#{courseName}, #{courseStartAt}, #{courseEndAt}, #{studentId})")
    void insertCourse(StudentCourse course);
}