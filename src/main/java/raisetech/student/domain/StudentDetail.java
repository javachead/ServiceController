package raisetech.student.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import raisetech.student.data.StudentCourse;
import raisetech.student.data.Student;

@Getter
@Setter
public
class StudentDetail{
    private Student student;
    private List<StudentCourse> studentCourses;
}