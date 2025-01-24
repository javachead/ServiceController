package raisetech.student.data;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * 学生のコース情報を管理するデータクラス。
 * 学生IDと紐づけて、受講中または受講完了したコースの情報を保持します。
 */

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