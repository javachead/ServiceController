//Repository はデータベースからデータを取得したり、保存したり、
// 削除したりするためのメソッドを定義する場として使用されます。
// Spring Data JPAを使用すると、リポジトリインタフェースを作成して
// 標準的なCRUD操作を簡単に実装することができます。

package raisetech.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import raisetech.student.data.StudentCourse;
import raisetech.student.data.Students;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Students, Long> {

    // 学生コースのリストを取得するメソッド
    @Query("SELECT sc FROM StudentCourse sc")
    List<StudentCourse> findAllStudentCourses();
}