package raisetech.student.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "student") // 対応するテーブルの名前を指定
//@Dataは保守性が悪いので、以下アノテーションへ変更
@ToString
@Getter
@Setter

public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String kanaName;
    private String nickName;
    private String email;
    private String area;
    private int age;
    private String sex;
    private String remark;
    private boolean isDeleted;
}