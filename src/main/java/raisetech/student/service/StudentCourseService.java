package raisetech.student.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

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

    // 新規コースを保存または更新する
    public void saveCourse(StudentCourse course) {
        Optional.ofNullable(course.getId())
                .ifPresentOrElse(
                        id -> {
                            // 既存コースが更新対象
                            log.info("コースID={} を更新します: {}", id, course);
                            studentCourseRepository.updateCourse(course);
                        },
                        () -> {
                            // 新規登録
                            log.info("新しいコースを登録します: {}", course);
                            studentCourseRepository.insertCourse(course);
                        }
                );
    }

    /**
     * 学生IDに基づきコース名を一括変更。
     * 注意: コース名がハードコーディングされており、すべて"Updated Course Name"に更新されるため、
     * 動的な更新要件には対応していません。
     */
    public List<StudentCourse> updateByStudentId(Integer studentId) {
        log.info("学生ID={} のコース情報を一括更新します", studentId);

        List<StudentCourse> courses = studentCourseRepository.findByStudentId(studentId.longValue());

        courses.forEach(course -> {
            course.setCourseName("Updated Course Name");
            studentCourseRepository.updateCourse(course);
        });

        log.info("コースの更新完了：更新数: {}", courses.size());
        return courses;
    }

    /**
     * 新規登録・既存更新のコース処理
     * このメソッドはリストで渡されたコースの中で、新しいものを登録し、既存のものを更新します。
     * @param newCourses 新規または更新対象のコースリスト
     * @param existingCourses 既存コースのリスト
     * @param studentId 学生ID (新規登録の際に使用)
     */
    private void processCourses(List<StudentCourse> targetCourses, List<StudentCourse> existingCourses,
                                Long studentId, Merger<StudentCourse> merger) {
        targetCourses.forEach(course -> {
            if (course.getId() == null) {
                log.info("新規登録コース: {}", course);
                course.setStudentId(studentId);
                studentCourseRepository.insertCourse(course);
            } else {
                log.info("更新対象コース: {}", course);
                merger.merge(existingCourses, course);
            }
        });
    }

    /**
     * 存在しないコースの削除処理
     * 渡された情報から削除対象の既存コースを判定し、該当するものを削除します。
     * 注意: この処理を行うことで、渡されたリストにないすべてのコースが削除されるため、
     * 入力データの正当性を事前に保証する必要があります。
     * @param newCourses 保存または更新されるコースのリスト
     * @param existingCourses 既存コースのリスト
     */
    private void removeUnusedCourses(List<StudentCourse> newCourses, List<StudentCourse> existingCourses) {
        existingCourses.stream()
                .filter(existingCourse -> newCourses.stream()
                        .noneMatch(c -> existingCourse.getId().equals(c.getId())))
                .forEach(unusedCourse -> {
                    log.info("削除対象コースID: {}", unusedCourse.getId());
                    studentCourseRepository.deleteCourse(unusedCourse.getId());
                });
    }

    /**
     * 学生IDに紐づくコース情報を保存または更新。
     * 注意:
     * - 新規コースは登録し、既存のコースは更新。
     * - 渡されたリストに存在しないコースは削除される。
     * - 入力リストが学生IDと整合性がとれている必要あり。
     * @param courses 保存または更新するコースリスト
     * @param studentId 学生ID
     */
    public void saveCourses(List<StudentCourse> courses, Long studentId) {
        log.info("学生ID {} に紐づくコース情報を保存または更新します。件数: {}", studentId, courses.size());

        // 学生に関連する既存のコース情報を取得
        List<StudentCourse> existingCourses = studentCourseRepository.findByStudentId(studentId);

        // 更新または新規登録
        processCourses(courses, existingCourses, studentId, (existingList, course) -> {
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
    interface Merger<T> {
        void merge(List<T> existingList, T target);
    }
}