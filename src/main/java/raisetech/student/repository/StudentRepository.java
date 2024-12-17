package raisetech.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raisetech.student.data.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 年齢範囲で学生を検索
    List<Student> findByAgeBetween(int startAge, int endAge);
}