package raisetech.student.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 学生のコース情報を管理するデータクラス。
 * 学生IDと紐づけて、受講中または受講完了したコースの情報を保持します。
 */

@Getter
@Setter
@ToString
@NoArgsConstructor

public class StudentCourse {

    private Long id; // 自動生成されることを想定しているためバリデーション不要

    private Long studentId; // 学生ID、他の関連情報に依存するためここではバリデーションを省略

    @NotBlank(message = "コース名は必須です")
    private String courseName; // 登録内容に必須とする

    private LocalDate courseStartAt; // 開始日

    private LocalDate courseEndAt; // 終了日
}
