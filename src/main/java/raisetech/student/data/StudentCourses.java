package raisetech.student.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "studentscourses")
@Data
@NoArgsConstructor
public class StudentCourses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 明示的にセッターメソッドを追加
    @Setter
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String courseName;
    private LocalDate courseStartAt;
    private LocalDate courseEndAt;

    public void setStudentId(Long studentId) {
        if (this.student == null) {
            this.student = new Student();
        }
        this.student.setId(studentId); // StudentのIDを直接設定
    }
}