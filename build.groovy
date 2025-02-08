plugins {
    id 'java'
    id 'war'
    id 'org.springframework.boot' version '3.3.4'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'application'
}
group = 'com.example'
version = '0.0.1-SNAPSHOT'
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}
application {
    mainClass = 'com.example.student.management.StudentManagementApplication'
}
repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'org.springframework.boot:spring-boot-starter'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    runtimeOnly 'com.mysql:mysql-connector-j'
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'
    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.withType(JavaCompile).configureEach {
    javaCompiler = javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
tasks.withType(Test).configureEach {
    useJUnitPlatform()
}
