package raisetech.student.data;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

/**
 * 学生情報を管理するクラス（DTO）。
 * 学生のプロパティを保持するデータコンテナであり、別途バリデーションや操作ロジックはサービス層で実装されることを前提としています。
 */

public class Student {

    private Long id;
    private String name;
    private String kanaName;
    private String nickname;
    private String email;
    private String area;
    private Integer age;
    private String sex;
    private String remark;
    private boolean isDeleted;
}