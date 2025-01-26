package raisetech.student.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import raisetech.student.data.StudentCourse;

@Mapper
public interface StudentCourseRepository {

    // ---------------- 学生IDに基づくコース情報を取得 ----------------
    /**
     * 特定の学生（studentId）に紐づくコース情報をすべて取得。
     * @param studentId 学生ID
     * @return 学生に関連付けられたすべてのコース情報のリスト
     */
    List<StudentCourse> findByStudentId(@Param("studentId") Long studentId);

    // ---------------- コース新規登録 ----------------

    /**
     * 新しいコースを追加。
     * @param course 登録するコース情報（ID は自動生成される）
     */
    void insertCourse(StudentCourse course);

    // ---------------- コース更新 ----------------

    /**
     * 特定のコース情報を更新。
     * @param course 更新するコース情報（ID が必要）
     */
    void updateCourse(StudentCourse course);

    // ---------------- 学生IDに基づいて複数のコース情報を更新 ----------------
    /**
     * 特定の学生に紐づく全てのコースをまとめて更新。
     * @param studentId 学生ID
     * @param courseName 変更後のコース名
     * @param courseStartAt 変更後のコース開始日
     * @param courseEndAt 変更後のコース終了日
     */
    void updateCoursesByStudentId(@Param("studentId") Long studentId,
                                  @Param("courseName") String courseName,
                                  @Param("courseStartAt") String courseStartAt,
                                  @Param("courseEndAt") String courseEndAt);

    // ---------------- コース削除 ----------------

    /**
     * 指定されたIDのコース情報を削除（物理削除）。
     * @param id 削除対象のコースID
     */
    void deleteCourse(@Param("id") Long id);
}