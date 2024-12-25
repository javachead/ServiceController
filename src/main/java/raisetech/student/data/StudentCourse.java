package raisetech.student.data;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class StudentCourse {
    private Long id;
    private String studentCourse;
    private String studentId;
    private String courseName;
    private LocalDate courseStartAt;
    private LocalDate courseEndAt;
}