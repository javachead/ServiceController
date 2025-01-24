package raisetech.student.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import raisetech.student.data.StudentCourse;

import java.util.List;

@Mapper
public interface StudentCourseRepository {

    // ---------------- 学生IDに基づくコース情報を取得 ----------------

    // 学生IDに基づくコースを取得（単体メソッド）
    @Select("SELECT * FROM student_courses WHERE student_id = #{studentId}")
    List<StudentCourse> findByStudentId(@Param("studentId") Long studentId);

    // ---------------- コース新規登録 ----------------

    // 新しいコースを挿入（IDが自動生成される）
    @Insert("INSERT INTO student_courses (course_name, course_start_at, course_end_at, student_id) " +
            "VALUES (#{courseName}, #{courseStartAt}, #{courseEndAt}, #{studentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCourse(StudentCourse course);

    // ---------------- コース更新 ----------------

    // コース情報を更新（単一のコース）
    @Update("UPDATE student_courses SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} " +
            "WHERE id = #{id}")
    void updateCourse(StudentCourse course);

    // 学生IDに基づいてコース情報を一括更新
    @Update("UPDATE student_courses " +
            "SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} " +
            "WHERE student_id = #{studentId}")
    void updateCoursesByStudentId(@Param("studentId") Long studentId,
                                  @Param("courseName") String courseName,
                                  @Param("courseStartAt") String courseStartAt,
                                  @Param("courseEndAt") String courseEndAt);

    // ---------------- コース削除 ----------------

    // コース情報を削除（IDに基づいて）
    @Delete("DELETE FROM student_courses WHERE id = #{id}")
    void deleteCourse(@Param("id") Long id);
}