package raisetech.student;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * アプリケーションのエントリーポイント (Spring Boot).
 * - @SpringBootApplication:
 * - Spring Boot がアプリケーション全体を構成する際に必要なアノテーション。
 * - scanBasePackages に "raisetech.student" を指定している意味:
 * - 明示的に "raisetech.student" パッケージ以下をコンポーネントスキャンの対象にしている。
 * - 将来的にパッケージ構成が変更された場合、この指定箇所も変更が必要になる点に注意。
 */
@OpenAPIDefinition(info = @Info(title = "学生管理システム", description = "学生管理APIドキュメント", version = "1.0.0", contact = @Contact(name = "API Support", email = "support@example.com")

))
@SpringBootApplication(scanBasePackages = "raisetech.student")
public class Application {
    public static void main(String[] args) {
        // Spring Boot アプリケーションを起動します。
        SpringApplication.run(Application.class, args);
    }
}
