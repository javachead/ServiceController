package raisetech.student.data;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

/**
 * 学生情報を管理するクラス（DTO）。
 * 学生のプロパティを保持するデータコンテナであり、
 * 別途バリデーションや操作ロジックはサービス層で実装されることを前提としています。
 */
public class Student {

    @Min(value = 1, message = "IDは1以上である必要があります")
    private Long id; // ID は数値型に適したバリデーションを使用

    @NotBlank(message = "名前は必須です")
    private String name;

    @NotBlank(message = "名前（カナ）は必須です")
    @Pattern(regexp = "^[\\u30A0-\\u30FF]+$", message = "名前（カナ）は全角カタカナのみ使用できます")
    private String kanaName;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ニックネームは半角英数字のみ使用できます")
    private String nickname; // ニックネームに形式制約を追加

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    @Pattern(regexp = "^(.*[都道府県])$", message = "住所エリアには「都」「道」「府」「県」を含めてください")
    @NotBlank(message = "住所（エリア）は必須です")
    private String area;

    @Min(value = 0, message = "年齢は0歳以上である必要があります")
    @Max(value = 150, message = "年齢は150歳以下である必要があります")
    private Integer age;

    @Pattern(regexp = "^(男性|女性|その他)?$", message = "性別は「男性」「女性」「その他」のいずれかを指定してください")
    private String sex;

    private String remark; // 備考は任意

    @NotNull(message = "isDeletedフラグは必須です")
    private Boolean deleted;

    // 学生が受講しているコースのリストを保持
    private List<StudentCourse> studentCourses;
}




