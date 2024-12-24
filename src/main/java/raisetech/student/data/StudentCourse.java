package raisetech.student.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class StudentCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String studentCourse; // camelCaseにリネーム
    private String studentId;
    private String courseName;
    private LocalDate courseStartAt;
    private LocalDate courseEndAt;
}