package raisetech.student.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.Student;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    /**
     * 特定の学生IDに関連付けられたコース情報のリストを取得します。
     *
     * @param studentId コース情報を取得する対象の学生の一意の識別子。
     * @return 指定された学生IDに関連付けられたStudentCourseオブジェクトのリスト。
     * @throws IllegalArgumentException 指定されたstudentIdがnullの場合にスローされます。
     */
    public List<StudentCourse> findByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生IDが指定されていません");
        }

        List<StudentCourse> studentCourses = studentCourseRepository.findByStudentId(studentId);

        // 空リストの場合に例外をスロー
        if (studentCourses.isEmpty()) {
            throw new IllegalStateException("データが見つかりません: 学生ID=" + studentId);
        }
        return studentCourses;
    }

    /**
     * 指定された学生に関連するコースのリストを保存します。このメソッドは、
     * 提供されたコースが必要なフィールドを満たしているか検証し、
     * 各コースに学生IDを割り当てます。
     *
     * @param savedStudent コースが保存される対象の学生オブジェクト。
     * @param courses      保存するコースのリスト。
     */
    public void saveCourses(Student savedStudent, List<StudentCourse> courses) {
        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException("コースを入力してください。");
        }
        // courses をループで検査し、null のものがあれば例外をスローする
        for (StudentCourse course : courses) {
            if (course.getCourseStartAt() == null) {
                throw new IllegalArgumentException("開始日が指定されていません");
            }
            if (course.getCourseStartAt().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("開始日は未来の日付を指定してください");
            }
            if (course.getCourseName() == null || course.getCourseName().isBlank()) {
                throw new IllegalArgumentException("コース名が空です");
            }
        }
        // 保存後の学生IDをコースに割り当て
        Long studentId = savedStudent.getId();
        courses.forEach(course -> course.setStudentId(studentId));

        // サービス内で保存を実施
        courseList(courses, studentId);
        log.info("学生 {} に関連するコースを保存しました。", studentId);
    }

    /**
     * 存在しないコースの削除処理
     * 渡された情報から削除対象の既存コースを判定し、該当するものを削除します。
     * 注意: この処理を行うことで、渡されたリストにないすべてのコースが削除されるため、
     * 入力データの正当性を事前に保証する必要があります。
     *
     * @param newCourses      保存または更新されるコースのリスト
     * @param existingCourses 既存コースのリスト
     */
    @Transactional
    public void removeUnusedCourses(List<StudentCourse> newCourses, List<StudentCourse> existingCourses) {
        // 新しいコースのIDをセットとして保持
        Set<Long> newCourseIds = newCourses.stream()
                .map(StudentCourse::getId)
                .filter(Objects::nonNull) // IDがnullでないものだけ処理
                .collect(Collectors.toSet());

        // 削除対象のコースを取得し、一括削除
        List<Long> toDeleteIds = existingCourses.stream()
                .map(StudentCourse::getId)
                .filter(id -> !newCourseIds.contains(id))
                .toList();

        toDeleteIds.forEach(studentCourseRepository::deleteCourse);
    }

    /**
     * 学生IDに紐づくコース情報を保存または更新。
     * 注意:
     * - 新規コースは登録し、既存のコースは更新。
     * - 渡されたリストに存在しないコースは削除される。
     * - 入力リストが学生IDと整合性がとれている必要あり。
     *
     * @param courses   保存または更新するコースリスト
     * @param studentId 学生ID
     */
    public void courseList(List<StudentCourse> courses, Long studentId) {
        log.info("学生ID {} に紐づくコース情報を保存または更新します。件数: {}", studentId, courses.size());

        // 学生に関連する既存のコース情報を取得
        List<StudentCourse> existingCourses = studentCourseRepository.findByStudentId(studentId);

        // 更新または新規登録
        courses.forEach(course -> {
            if (course.getId() == null) {
                // 新規登録
                log.info("新規登録コース: {}", course);
                course.setStudentId(studentId);
                studentCourseRepository.insertCourse(course);
            } else {
                // 更新処理
                log.info("更新対象コース: {}", course);
                existingCourses.stream()
                        .filter(existing -> existing.getId().equals(course.getId()))
                        .findFirst()
                        .ifPresentOrElse(existing -> {
                            // 更新フィールドのマージ
                            existing.setCourseName(course.getCourseName());
                            existing.setCourseStartAt(course.getCourseStartAt());
                            existing.setCourseEndAt(course.getCourseEndAt());
                            studentCourseRepository.updateCourse(existing);
                        }, () -> {
                            // 指定されたIDのコースが見つからなければエラー
                            throw new IllegalArgumentException("該当するコースIDが見つかりません: ID=" + course.getId());
                        });
            }
        });
        // 入力リストに存在しない既存データを削除
        List<Long> targetIds = courses.stream()
                .map(StudentCourse::getId)
                .toList(); // 入力リストのIDリスト
        existingCourses.stream()
                .filter(existing -> !targetIds.contains(existing.getId()))
                .forEach(courseToDelete -> {
                    log.info("削除対象コース: {}", courseToDelete);
                    studentCourseRepository.deleteCourse(courseToDelete.getId());
                });
    }
}
