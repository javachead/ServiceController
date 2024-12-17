package raisetech.student.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter; // Lombokによるゲッターメソッドの自動生成
import lombok.Setter; // Lombokによるセッターメソッドの自動生成

@Setter
@Getter
@Entity
@Table(name = "student") // 対応するテーブルの名前を指定
@Data
public
class Student{
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