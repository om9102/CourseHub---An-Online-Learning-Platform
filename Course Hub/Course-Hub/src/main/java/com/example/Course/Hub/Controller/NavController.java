package com.example.Course.Hub.Controller;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.SERVICES.Trainer.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NavController {


    @Autowired
    TrainerService tService;



    //Authenticate
    @GetMapping("/")
    public String indexPage(Model model) {

        List<Course> all = tService.getAllCourses();

        // Show max 6 courses on landing page preview
        List<Course> preview = all.size() > 6 ? all.subList(0, 6) : all;
        model.addAttribute("courses", preview);
        model.addAttribute("courseCount", all.size());


        // Count free courses for stats section
      long freeCount = all.stream()
          .filter(c -> "free".equalsIgnoreCase(c.getCourseType()))
          .count();
      model.addAttribute("freeCount", freeCount);


        return "Authenticate/index";
    }





    @GetMapping("/login")
    public String login(){
        return "Authenticate/login";
    }


    @GetMapping("/signup")
    public String signup(){
        return "Authenticate/SignUp";
    }



    @GetMapping("/public-courses")
    public String publicCourses(Model model) {
        List<Course> courses = tService.getAllCourses();

        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());

        // count free courses for stats banner                        │
        long freeCount = courses.stream()
            .filter(c -> "free".equalsIgnoreCase(c.getCourseType())).count();
      model.addAttribute("freeCount", freeCount);


        /*return "Test/public_courses";*/
        return "Authenticate/public_Courses";

    }





















    // Home Pages
    @GetMapping("/trainer-home")
    public String trainer_Home(){
        return "Home/trainer_Home";
    }
























    @GetMapping("/add-course")
    public String add_Course(){
        return "Course/add_Course";
    }


    @GetMapping("/add-Lesson")
    public String add_Lesson(){
        return "Home/add_Lession";
    }

    @GetMapping("/trainer_course_lesson")
    public String trainer_Course_Lesson(){
        return "Home/trainer_courses_lesson";
    }

    @GetMapping("/student-profile")
    public String student_profile(){
        return "Home/student_Profile";
    }

    @GetMapping("/trainer-profile")
    public String trainer_profile(){
        return "Home/trainer_Profile";
    }




}
