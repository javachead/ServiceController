package raisetech.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.domain.StudentDetail;
import raisetech.student.dto.StudentResponse;
import raisetech.student.exception.StudentNotFoundException;
import raisetech.student.handler.GlobalExceptionHandler;
import raisetech.student.service.StudentCourseService;
import raisetech.student.service.StudentDetailService;
import raisetech.student.service.StudentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private StudentDetailService studentDetailService;

    @MockBean
    private StudentCourse studentCourse;

    @MockBean
    private StudentCourseService studentCourseService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void 全学生情報を取得する() throws Exception {
        List<StudentDetail> mockStudents = Collections.singletonList(new StudentDetail());
        when(studentDetailService.findAllStudentDetails()).thenReturn(mockStudents);

        mockMvc.perform(get("/api/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("学生一覧を取得しました"));

        verify(studentDetailService, times(1)).findAllStudentDetails();
    }

    @Test
    public void 新規学生情報およびコース情報を登録する() throws Exception {
        // ObjectMapperにJSR310モジュールを登録
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // テスト対象のStudentオブジェクト
        Student student = new Student(null, "山田太郎", "ヤマダタロウ", "taro123",
                "updated@example.com", "東京都", 20, "男性", "備考", false, null);

        List<StudentCourse> courses = List.of(
                new StudentCourse(null, null, "Java", LocalDate.of(2023, 4, 1), LocalDate.of(2023, 7, 15))
        );

        student.setStudentCourses(courses);

        // Mockの挙動を設定
        when(studentService.save(isNull(), any(Student.class))).thenAnswer(invocation -> {
            Student capturedStudent = invocation.getArgument(1);
            capturedStudent.setId(1L); // IDの設定
            capturedStudent.getStudentCourses().forEach(course -> course.setStudentId(1L));
            return capturedStudent;
        });

        // POSTリクエストと期待する結果の確認
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("学生情報および関連コースが正常に保存されました"))
                .andExpect(jsonPath("$.student.id").value(1))
                .andExpect(jsonPath("$.student.name").value("山田太郎"))
                .andExpect(jsonPath("$.student.studentCourses[0].studentId").value(1))
                .andExpect(jsonPath("$.student.studentCourses[0].courseName").value("Java"))
                .andExpect(jsonPath("$.student.studentCourses[0].courseStartAt").value("2023-04-01"))
                .andExpect(jsonPath("$.student.studentCourses[0].courseEndAt").value("2023-07-15"));
    }

    @Test
    public void 学生情報をID指定で取得するテスト() throws Exception {
        Long studentId = 1L;

        // モックオブジェクトの準備
        Student expectedStudent = new Student(
                studentId,
                "山田太郎",
                "ヤマダタロウ",
                "taro123",
                "taro@example.com",
                "東京都",
                20,
                "男性",
                "備考",
                false,
                null
        );
        StudentResponse expectedResponse = new StudentResponse(
                "指定されたIDの学生を取得しました",
                expectedStudent.getId()
        );
        // サービス層のモック設定
        when(studentService.getStudentById(eq(studentId))).thenReturn(expectedStudent);

        // API呼び出し
        String jsonResponse = mockMvc.perform(get("/api/students/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        // JSONレスポンスをオブジェクトに変換
        StudentResponse actualResponse = objectMapper.readValue(jsonResponse, StudentResponse.class);

        // オブジェクト全体を比較
        assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    public void 学生削除リクエストが正常に処理されるテスト() throws Exception {
        Long studentId = 1L;

        // ----- 1. 正常系 (Happy Path) -----
        doNothing().when(studentService).deleteStudentById(studentId);

        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isOk());
        verify(studentService, times(1)).deleteStudentById(studentId);

        // ----- 2. 異常系 (例外処理) -----
        doThrow(new StudentNotFoundException("学生が存在しません: ID = " + studentId))
                .when(studentService).deleteStudentById(studentId);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNotFound()) // HTTPステータス404が期待される
                .andExpect(jsonPath("$.message").value("学生データが見つかりませんでした。"));
    }

    @Test
    void 学生情報更新テスト() throws Exception {
        Long studentId = 1L;

        // 必須データを設定
        Student student = new Student();
        student.setId(studentId);
        student.setName("山田太郎");
        student.setKanaName("ヤマダタロウ");
        student.setEmail("yamada@example.com");
        student.setArea("東京都");
        student.setDeleted(false);

        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);
        studentDetail.setStudentCourses(Collections.emptyList());

        when(studentService.save(eq(studentId), any())).thenReturn(student);

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDetail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("学生データが正常に更新されました"))
                .andExpect(jsonPath("$.student").value(studentId));
    }

    @Test
    void 存在しない学生IDを指定した場合にStudentNotFoundExceptionをスローする() throws Exception {
        Long invalidStudentId = 99999L; // バリデーションを満たす無効なID
        when(studentService.getStudentById(eq(invalidStudentId)))
                .thenThrow(new StudentNotFoundException("学生が存在しません: ID = " + invalidStudentId));

        mockMvc.perform(get("/api/students/{id}", invalidStudentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // ステータス404
                .andExpect(jsonPath("$.message").value("学生データが見つかりませんでした。"));
    }
}
