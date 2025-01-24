package raisetech.student.exception;

/**
 * 学生が見つからない場合にスローされる例外クラス。
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}