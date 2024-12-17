package raisetech.student.controller.converter;

import org.springframework.stereotype.Component;
import raisetech.student.data.StudentCourse;
import raisetech.student.data.Student;

import java.util.ArrayList;
import java.util.List;

@Component
public
class StudentConverter{
    public
    List<StudentCourse> convertStudentCourses(Student student, List<StudentCourse> studentCourses) {
        List<StudentCourse> filteredCourses = new ArrayList<>();
        for (StudentCourse studentCourse : studentCourses) {
            if (student.getId().equals(studentCourse.getId())) {
                filteredCourses.add(studentCourse);
            }
        }
        return filteredCourses;
    }
}