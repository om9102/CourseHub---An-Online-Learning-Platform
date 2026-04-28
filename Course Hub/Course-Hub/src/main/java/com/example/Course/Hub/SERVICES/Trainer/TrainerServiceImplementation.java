package com.example.Course.Hub.SERVICES.Trainer;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.ENTITY.Lesson;
import com.example.Course.Hub.REPOSITORY.CourseRepository;
import com.example.Course.Hub.REPOSITORY.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImplementation implements TrainerService {

    @Autowired
    CourseRepository cRepo;

    @Autowired
    LessonRepository lRepo;




    @Override
    public String addCourse(Course c) {
        cRepo.save(c);
        return "Course Added Successfully!";
    }





    @Override
    public String addLesson(Lesson l) {
       lRepo.save(l);
       return "Lesson Added Successfully";
    }



    @Override
    public Course getCourse(String course_id) {
        return cRepo.findByCourseId(course_id);
    }


    @Override
    public List<Course> getAllCourses() {
        return cRepo.findAll();  // findAll() is free from JpaRepository
    }






    @Override
    public List<Course> getCoursesByTrainer(String trainerName) {


        /**
         * NEW METHOD — fetch only courses by a specific trainer.
         *
         * HOW IT WORKS:
         *   cRepo.findByTrainerName(trainerName)
         *   → JPA auto-generates: SELECT * FROM course WHERE trainer_name = ?
         *
         * USAGE in NavController (for trainer's My Courses page):
         *   Users trainer = (Users) session.getAttribute("user");
         *   String fullName = trainer.getFirstname() + " " + trainer.getLastname();
         *   List<Course> courses = tService.getCoursesByTrainer(fullName);
         *
         * IMPORTANT: trainerName must match EXACTLY what the trainer typed
         * in the Add Course form. Recommend auto-filling from session in the form:
         *   th:value="${session.user.firstname} + ' ' + ${session.user.lastname}"
         */


        return cRepo.findByTrainerName(trainerName);
    }



}
