package raisetech.student.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private Integer id;

    @NotBlank(message = "名前は必須入力です。空白やnullは許可されません。")
    @Size(max = 100, message = "名前は100文字以内で入力してください。")
    private String name;

    @NotBlank(message = "カナ名は必須入力です。空白やnullは許可されません。")
    @Size(max = 100, message = "カナ名は100文字以内で入力してください。")
    private String kanaName;

    @Size(max = 100, message = "ニックネームは100文字以内で入力してください。")
    private String nickname;

    @NotBlank(message = "メールアドレスは必須入力です。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    private String email;

    @NotBlank(message = "地域情報は必須入力です。")
    private String area;

    @NotNull(message = "年齢は必須入力です。")
    @Min(value = 0, message = "年齢は0以上の値を入力してください。")
    @Max(value = 150, message = "年齢は150以下の値を入力してください。")
    private Integer age;

    private String sex;

    @Size(max = 255, message = "備考は255文字以内で入力してください。")
    private String remark;
}