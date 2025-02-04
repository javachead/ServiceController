package raisetech.student.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional // このクラスのすべてのメソッドにトランザクションを適用
@Slf4j
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    // 学生IDに基づくコース情報を取得
    public List<StudentCourse> findByStudentId(Long studentId) {
        log.info("学生ID={} のコースを取得します。", studentId);
        return studentCourseRepository.findByStudentId(studentId);
    }

    /**
     * 新規登録・既存更新のコース処理
     * このメソッドはリストで渡されたコースの中で、新しいものを登録し、既存のものを更新します。
     *
     * @param targetCourses   新規または更新対象のコースリスト
     * @param existingCourses 既存コースのリスト
     * @param studentId       学生ID (新規登録の際に使用)
     * @param courseUpdater   新規・更新処理を行うための関数型インターフェース
     */
    private void handleCourses(List<StudentCourse> targetCourses, List<StudentCourse> existingCourses,
                               Long studentId, CourseUpdater<StudentCourse> courseUpdater) {
        targetCourses.forEach(course -> {
            if (course.getId() == null) {
                log.info("新規登録コース: {}", course);
                course.setStudentId(studentId);
                studentCourseRepository.insertCourse(course);
            } else {
                log.info("更新対象コース: {}", course);
                // CourseUpdaterのmergeメソッドを使用
                courseUpdater.merge(existingCourses, course);
            }
        });
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
    private void removeUnusedCourses(List<StudentCourse> newCourses, List<StudentCourse> existingCourses) {
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
    public void saveCourses(List<StudentCourse> courses, Long studentId) {
        log.info("学生ID {} に紐づくコース情報を保存または更新します。件数: {}", studentId, courses.size());

        // 学生に関連する既存のコース情報を取得
        List<StudentCourse> existingCourses = studentCourseRepository.findByStudentId(studentId);

        // 更新または新規登録
        handleCourses(courses, existingCourses, studentId, (existingList, course) -> {
            existingList.stream()
                    .filter(existing -> existing.getId().equals(course.getId()))
                    .findFirst()
                    .ifPresentOrElse(existing -> {
                        existing.setCourseName(course.getCourseName());
                        existing.setCourseStartAt(course.getCourseStartAt());
                        existing.setCourseEndAt(course.getCourseEndAt());
                        studentCourseRepository.updateCourse(existing);
                    }, () -> {
                        throw new IllegalArgumentException("該当するコースIDが見つかりません: ID=" + course.getId());
                    });
        });

        // 削除対象のコースを判定して削除
        removeUnusedCourses(courses, existingCourses);
        log.info("コース保存および削除処理完了。");
    }

    // 関数型インターフェース（汎用化）
    @FunctionalInterface
    interface CourseUpdater<T> {
        void merge(List<T> existingList, T target);
    }
}
