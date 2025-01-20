package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import lombok.extern.slf4j.Slf4j; // ログ機能を簡略化

import java.util.List;

@Service
@Transactional // このクラスのすべてのメソッドにトランザクションを適用
@Slf4j
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    //学生IDに基づくコース情報を取得
    public List<StudentCourse> findByStudentId(Long studentId) {
        log.info("学生ID {} に基づいてコース情報を取得します。", studentId);
        List<StudentCourse> courses = studentCourseRepository.findByStudentId(studentId);
        log.info("コース件数: {}", courses.size());
        return courses;
    }

    //学生IDに紐づくコース情報を保存または更新
    public void saveCourses(List<StudentCourse> courses, Long studentId) {
        log.info("学生ID {} に基づくコース情報を保存または更新します。件数: {}", studentId, courses.size());

        // 学生に関連する既存のコース情報を取得
        List<StudentCourse> existingCourses = studentCourseRepository.findByStudentId(studentId);

        // 更新または新規登録
        processNewAndUpdatedCourses(courses, existingCourses, studentId);

        // 削除対象のコースを判定して削除
        removeNonExistentCourses(courses, existingCourses);

        log.info("すべての処理が完了しました。");
    }

    //コース情報の更新処理
    public void updateCourses(Long studentId, String courseName, String courseStartAt, String courseEndAt) {
        log.info("学生ID {} のコース情報を一括更新します。", studentId);
        studentCourseRepository.updateCoursesByStudentId(studentId, courseName, courseStartAt, courseEndAt);
        log.info("コース情報の一括更新が完了しました。");
    }

    //コース名を一括更新
    public List<StudentCourse> updateByStudentId(Integer studentId) {
        log.info("学生ID {} でコース名を一括変更します。", studentId);

        // 学生IDでコースを取得
        List<StudentCourse> courses = studentCourseRepository.findByStudentId(studentId.longValue());

        for (StudentCourse course : courses) {
            // コース名を更新
            course.setCourseName("Updated Course Name");
            studentCourseRepository.updateCourse(course);
        }

        log.info("コース名の更新完了。更新件数: {}", courses.size());
        return courses;
    }

    //新規または更新対象のコースを処理
    private void processNewAndUpdatedCourses(List<StudentCourse> newCourses, List<StudentCourse> existingCourses, Long studentId) {
        for (StudentCourse newCourse : newCourses) {
            if (newCourse.getId() == null) {
                // 新規登録
                log.info("新しいコースを登録: {}", newCourse);
                newCourse.setStudentId(studentId); // 学生IDを紐付け
                studentCourseRepository.insertCourse(newCourse);
            } else {
                // 既存コースの更新
                log.info("更新対象のコースID: {}", newCourse.getId());
                StudentCourse existingCourse = existingCourses.stream()
                        .filter(c -> c.getId().equals(newCourse.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("該当するコースが見つかりません: ID=" + newCourse.getId()));

                // 情報を更新
                existingCourse.setCourseName(newCourse.getCourseName());
                existingCourse.setCourseStartAt(newCourse.getCourseStartAt());
                existingCourse.setCourseEndAt(newCourse.getCourseEndAt());
                studentCourseRepository.updateCourse(existingCourse);
            }
        }
    }

    private void removeNonExistentCourses(List<StudentCourse> newCourses, List<StudentCourse> existingCourses) {
        for (StudentCourse existingCourse : existingCourses) {
            boolean shouldRemove = newCourses.stream()
                    .noneMatch(c -> c.getId() != null && c.getId().equals(existingCourse.getId()));
            if (shouldRemove) {
                // 削除対象と判定
                log.info("削除するコースID: {}", existingCourse.getId());
                studentCourseRepository.deleteCourse(existingCourse.getId());
            }
        }
    }
}
