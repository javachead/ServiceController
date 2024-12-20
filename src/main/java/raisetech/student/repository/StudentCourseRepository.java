package raisetech.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import raisetech.student.data.StudentCourse;

import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    // 学生コースのリストを取得するメソッド
    @Query("SELECT sc FROM StudentCourse sc")
    List<StudentCourse> findAllStudentCourses();
}