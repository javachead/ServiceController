package raisetech.student.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.data.StudentCourse;
import raisetech.student.repository.StudentCourseRepository;

import java.util.List;

@Service
@Transactional
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;

    public StudentCourseService(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    // 学生IDに基づくコース情報を取得
    public List<StudentCourse> findByStudentId(Long studentId) {
        return studentCourseRepository.findByStudentId(studentId);
    }

    // 学生のコース情報を保存
    public void saveCourses(List<StudentCourse> courses, Long studentId) {
        // 現在の学生に紐づけられたすべてのコースを取得
        List<StudentCourse> existingCourses = studentCourseRepository.findByStudentId(studentId);

        // 新しいまたは更新対象のコース
        for (StudentCourse newCourse : courses) {
            if (newCourse.getId() == null) {
                // 新規挿入
                newCourse.setStudentId(studentId); // 関連づけのためIDを設定
                studentCourseRepository.insertCourse(newCourse);
            } else {
                // 既存データの更新
                StudentCourse existingCourse = existingCourses.stream()
                        .filter(c -> c.getId().equals(newCourse.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("該当コースが見つかりません。ID: " + newCourse.getId()));

                // フィールドを更新
                existingCourse.setCourseName(newCourse.getCourseName());
                existingCourse.setCourseStartAt(newCourse.getCourseStartAt());
                existingCourse.setCourseEndAt(newCourse.getCourseEndAt());
                studentCourseRepository.updateCourse(existingCourse);
            }
        }

        // 削除対象を特定（現在のリストから存在しないもの）
        for (StudentCourse existingCourse : existingCourses) {
            boolean isRemoved = courses.stream()
                    .noneMatch(c -> c.getId() != null && c.getId().equals(existingCourse.getId()));
            if (isRemoved) {
                studentCourseRepository.deleteCourse(existingCourse.getId());
            }
        }
    }

    // 学生IDに紐づく複数のコースを更新する
    public void updateCourses(Long studentId, String courseName, String courseStartAt, String courseEndAt) {
        studentCourseRepository.updateCoursesByStudentId(studentId, courseName, courseStartAt, courseEndAt);
    }

    public List<StudentCourse> updateByStudentId(Integer id) {
        // 学生IDを使用して紐づくコース情報を取得
        List<StudentCourse> courses = studentCourseRepository.findByStudentId(id.longValue());

        // 更新が必要な場合は処理（例: 日付の更新や名前変更があれば行う）
        for (StudentCourse course : courses) {
            course.setCourseName("Updated Course Name");
            studentCourseRepository.updateCourse(course); // リポジトリで更新
        }

        // 更新済みのコース情報を返す
        return courses;
    }
}