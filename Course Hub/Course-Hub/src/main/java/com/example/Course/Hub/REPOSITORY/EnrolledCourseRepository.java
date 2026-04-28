package com.example.Course.Hub.REPOSITORY;


import com.example.Course.Hub.ENTITY.EnrolledCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * EnrolledCourseRepository — JPA repository for EnrolledCourse entity.
 *
 * Spring Data JPA auto-implements all methods below.
 * No SQL needed — method names are read by JPA to generate queries.
 *
 * ════════════════════════════════════════════════════════════
 * METHOD BREAKDOWN:
 *
 * findByStudentEmail(email)
 *   SQL: SELECT * FROM enrolled_course WHERE student_email = ?
 *   Used: StudentController.myCourses() → get all enrolled rows for this student
 *
 * existsByStudentEmailAndCourseId(email, courseId)
 *   SQL: SELECT COUNT(*) > 0 FROM enrolled_course
 *        WHERE student_email = ? AND course_id = ?
 *   Used: CartController.checkout() → duplicate enrollment check
 *   Returns true if already enrolled → skip saving again
 *
 * findByStudentEmailAndCourseId(email, courseId)
 *   SQL: SELECT * FROM enrolled_course
 *        WHERE student_email = ? AND course_id = ?
 *   Used: if you ever need to fetch the specific enrollment row
 * ════════════════════════════════════════════════════════════
 */




public interface EnrolledCourseRepository extends JpaRepository<EnrolledCourse, Integer> {





    /**
     * Get all courses enrolled by a specific student.
     * Used in StudentController.myCourses() to populate the My Courses page.
     *
     * @param studentEmail  The email of the logged-in student
     * @return List of EnrolledCourse rows for this student
     */
    List<EnrolledCourse> findByStudentEmail(String studentEmail);






    /**
     * Check if a student is already enrolled in a specific course.
     * Used in CartController.checkout() to prevent duplicate enrollment.
     *
     * @param studentEmail  Student's email
     * @param courseId      Course ID to check
     * @return true if already enrolled, false otherwise
     */
    boolean existsByStudentEmailAndCourseId(String studentEmail, String courseId);
}
