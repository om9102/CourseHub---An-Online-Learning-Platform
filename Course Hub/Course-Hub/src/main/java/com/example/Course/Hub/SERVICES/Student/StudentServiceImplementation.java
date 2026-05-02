package com.example.Course.Hub.SERVICES.Student;


import com.example.Course.Hub.ENTITY.Lesson;
import com.example.Course.Hub.REPOSITORY.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;




@Service
public class StudentServiceImplementation implements StudentServices {


    @Autowired
    LessonRepository lRepo;






    /**
     * DEPRECATED — still implemented for backward compatibility.
     * Uses the raw JPA primary key (auto-generated 'id', not 'lessonId').
     * This is ambiguous — don't use this for lesson fetching.
     */

    @Override
    @Deprecated
    public Lesson getLessonId(int lessonId) {
        return lRepo.findById(lessonId).get();
    }







    /**
     * CORRECT lesson fetch — uses courseId + lessonId together.
     *
     * This calls:
     *   LessonRepository.findByCourse_CourseIdAndLessonId(courseId, lessonId)
     *
     * Which runs:
     *   SELECT * FROM lesson l
     *   JOIN course c ON l.course_id = c.id
     *   WHERE c.course_id = ? AND l.lesson_id = ?
     *
     * Returns null if the lesson is not found (controller handles this).
     *
     * @param courseId  "JAVA101", "PY201", etc.
     * @param lessonId  1, 2, 3, ... (trainer-defined, per-course numbering)
     */
    @Override
    public Lesson getLessonByCourseAndId(String courseId, int lessonId) {
        return lRepo.findByCourse_CourseIdAndLessonId(courseId, lessonId)
                .orElse(null);
    }





    /**
     * Fetch all lessons for a course — sorted by lessonId.
     * Used to populate the sidebar lesson list on the watch page.
     *
     * @param courseId  "JAVA101", "PY201", etc.
     */
    @Override
    public List<Lesson> getLessonsByCourse(String courseId) {
        List<Lesson> lessons = lRepo.findByCourse_CourseId(courseId);
        // Sort by lessonId so they appear in correct order
        lessons.sort((a, b) -> Integer.compare(a.getLessonId(), b.getLessonId()));
        return lessons;
    }


    /**
     * Fetch all lessons for a course — sorted by lessonId.
     * Used to populate the sidebar lesson list on the watch page.
     *
     * @param courseId  "JAVA101", "PY201", etc.
     */





}
