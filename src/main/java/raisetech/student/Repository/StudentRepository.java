package raisetech.student.Repository;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;

@Mapper
public
interface StudentRepository {
    @Select("SELECT * FROM students")
    List<Student> search();
    @Select("SELECT * FROM student_courses")
    List<StudentCourse> searchCourseList();
}