package raisetech.student.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.data.StudentCourse;
import java.util.List;

@Mapper
public interface StudentCourseRepository {

    // ---------------- 学生IDに基づくコース情報を取得 ----------------

    /**
     * 特定の学生（studentId）に紐づくコース情報をすべて取得。
     * @param studentId 学生ID
     * @return 学生に関連付けられたすべてのコース情報のリスト
     */
    @Select("SELECT * FROM student_courses WHERE student_id = #{studentId}")
    List<StudentCourse> findByStudentId(@Param("studentId") Long studentId);

    // ---------------- コース新規登録 ----------------

    /**
     * 新しいコースを追加。挿入時に自動生成された ID は引数として渡されるオブジェクトに更新される。
     * @param course 登録するコース情報（ID は自動生成される）
     * 複数のスレッドから同時に呼び出される場合、意図しないデータが格納されることがあるため注意。
     */
    @Insert("INSERT INTO student_courses (course_name, course_start_at, course_end_at, student_id) " +
            "VALUES (#{courseName}, #{courseStartAt}, #{courseEndAt}, #{studentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCourse(StudentCourse course);

    // ---------------- コース更新 ----------------

    /**
     * 特定のコース情報を更新。
     * @param course 更新するコース情報（ID が必要）
     */
    @Update("UPDATE student_courses SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} " +
            "WHERE id = #{id}")
    void updateCourse(StudentCourse course);

    /**
     * 特定の学生に紐づく全てのコースをまとめて更新。
     * 注意: コース単位ではなく学生単位で変更が必要な場合に使用。
     * ただし、すべてのコースが同じ値で更新される点に注意が必要。
     * @param studentId 学生ID
     * @param courseName 変更後のコース名
     * @param courseStartAt 変更後のコース開始日
     * @param courseEndAt 変更後のコース終了日
     */
    @Update("UPDATE student_courses " +
            "SET course_name = #{courseName}, course_start_at = #{courseStartAt}, course_end_at = #{courseEndAt} " +
            "WHERE student_id = #{studentId}")
    void updateCoursesByStudentId(@Param("studentId") Long studentId,
                                  @Param("courseName") String courseName,
                                  @Param("courseStartAt") String courseStartAt,
                                  @Param("courseEndAt") String courseEndAt);

    // ---------------- コース削除 ----------------

    /**
     * 指定されたIDのコース情報を削除（物理削除を行う）。
     * 注意: データを復元できないため、通常は論理削除が推奨される。
     * @param id 削除対象のコースID
     */
    @Delete("DELETE FROM student_courses WHERE id = #{id}")
    void deleteCourse(@Param("id") Long id);
}