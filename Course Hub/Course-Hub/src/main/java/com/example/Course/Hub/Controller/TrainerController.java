package com.example.Course.Hub.Controller;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.ENTITY.Lesson;
import com.example.Course.Hub.SERVICES.Trainer.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class TrainerController {

    @Autowired
    TrainerService tService;





    @PostMapping("/addCourse")
    public String addCourse(@RequestParam("courseId") String courseId,
                            @RequestParam("courseName") String courseName,
                            @RequestParam("courseShortDescription") String courseShortDescription,
                            @RequestParam("trainerName") String trainerName,
                            @RequestParam("coursePrice") String coursePrice,
                            @RequestParam("courseType") String courseType
                      ){

        Course c = new Course();

        c.setCourseId(courseId);
        c.setCourseName(courseName);
        c.setCourseShortDescription(courseShortDescription);
        c.setTrainerName(trainerName);
        c.setCoursePrice(coursePrice);
        c.setCourseType(courseType);

        tService.addCourse(c);

        return "redirect:/trainer-home";

    }








    @PostMapping("/addLesson")
    public String addLesson(@RequestParam("lessonId") int lessonId,
                            @RequestParam("courseId") String courseId,
                            @RequestParam("lessonName") String lessonName,
                            @RequestParam("lessonTopic") String lessonTopic,
                            @RequestParam("lessonDescription") String lessonDescription,
                            @RequestParam("lessonVideoLink") String lessonVideoLink

    ){

        Course course = tService.getCourse(courseId);

        Lesson l = new Lesson(lessonId , lessonName , lessonTopic , lessonDescription, lessonVideoLink , course);

        tService.addLesson(l);

        course.getLessons().add(l);


        return "redirect:/trainer-home";



    }




































}
