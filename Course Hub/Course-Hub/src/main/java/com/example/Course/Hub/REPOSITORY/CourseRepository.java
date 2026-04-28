package com.example.Course.Hub.REPOSITORY;

import com.example.Course.Hub.ENTITY.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course , Integer> {



    /**
     * CourseRepository — JPA repository for Course entity.
     *
     * Spring Data JPA auto-implements all these methods.
     * No SQL needed — JPA reads the method name and generates the query.
     *
     * findByTrainerName("Arjun Patel")
     *   → SELECT * FROM course WHERE trainer_name = 'Arjun Patel'
     *   → Used in trainer's "My Courses" page
     */

    Course findByCourseId(String courseId);





    /**
     * Find all courses created by a specific trainer.
     * Used in NavController.myCourses() for the trainer dashboard.
     *
     * THYMELEAF USAGE: Called with trainer's full name from session:
     *   String name = user.getFirstname() + " " + user.getLastname();
     *   List<Course> courses = cRepo.findByTrainerName(name);
     *
     * IMPORTANT: The name must match EXACTLY what was typed in Add Course form.
     * Recommend auto-filling the trainer name field in add_Course.html:
     *   th:value="${session.user.firstname} + ' ' + ${session.user.lastname}"
     */



    List<Course> findByTrainerName(String trainerName);

}
