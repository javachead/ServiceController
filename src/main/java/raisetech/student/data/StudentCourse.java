package raisetech.student.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class StudentCourse {

    private Long id;
    private Long studentId;
    private String courseName;
    private LocalDate courseStartAt;
    private LocalDate courseEndAt;
}