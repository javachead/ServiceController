package raisetech.student.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import raisetech.student.data.StudentCourse;
import raisetech.student.data.Student;

@Getter
@Setter
public class StudentDetail {
    private Student student = new Student();
    private List<StudentCourse> studentCourses = new ArrayList<>();

    public void addCourses(List<StudentCourse> courses) {
        this.studentCourses.addAll(courses != null ? courses : Collections.emptyList()); // null チェックの簡素化
    }
}