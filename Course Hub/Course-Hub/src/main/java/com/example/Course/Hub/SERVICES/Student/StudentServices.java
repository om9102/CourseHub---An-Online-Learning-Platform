package com.example.Course.Hub.SERVICES.Student;


import com.example.Course.Hub.ENTITY.Lesson;

import java.util.List;

public interface StudentServices {



    /**
     * DEPRECATED — DO NOT USE
     * Fetching by lessonId alone is ambiguous when multiple courses
     * have lessons with the same lessonId number.
     * Use getLessonByCourseAndId(courseId, lessonId) instead.
     */

    @Deprecated
    Lesson getLessonId(int lessonId);



    /**
     * NEW — Correct way to fetch a specific lesson.
     * Uses BOTH courseId and lessonId to unambiguously find the lesson.
     *
     * @param courseId  The business courseId (e.g. "JAVA101")
     * @param lessonId  The trainer-defined lesson number (e.g. 1)
     * @return The correct Lesson, or null if not found
     */
    Lesson getLessonByCourseAndId(String courseId, int lessonId);





    /**
     * Get all lessons for a specific course.
     * Used by watchLesson() to populate the sidebar lesson list.
     *
     * @param courseId  The business courseId (e.g. "JAVA101")
     * @return List of all lessons for this course
     */
    List<Lesson> getLessonsByCourse(String courseId);

}
