package com.example.Course.Hub.SERVICES.Trainer;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.ENTITY.Lesson;

import java.util.List;

public interface TrainerService {

    String addCourse(Course c);

    String addLesson(Lesson l);

    Course getCourse(String course_id);

    List<Course> getAllCourses();

    List<Course> getCoursesByTrainer(String trainerName);

}
