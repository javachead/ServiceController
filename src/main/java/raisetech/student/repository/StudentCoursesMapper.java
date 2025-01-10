package raisetech.student.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.StudentCourses;

import java.util.List;

@Mapper
public interface StudentCoursesMapper {

    // 特定の学生IDに関連するコースを取得する
    @Select("SELECT * FROM studentscourses WHERE student_id = #{studentId}")
    List<StudentCourses> findByStudentId(Long studentId); // メソッド名を修正

    // 新しいコースを挿入する
    @Insert("INSERT INTO studentscourses (course_name, course_start_at, course_end_at, student_id) " +
            "VALUES (#{courseName}, #{courseStartAt}, #{courseEndAt}, #{student.id})")
    void insert(StudentCourses studentCourses);
}