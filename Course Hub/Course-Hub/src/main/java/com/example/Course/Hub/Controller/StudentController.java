package com.example.Course.Hub.Controller;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.ENTITY.EnrolledCourse;
import com.example.Course.Hub.ENTITY.Lesson;
import com.example.Course.Hub.ENTITY.Users;
import com.example.Course.Hub.REPOSITORY.EnrolledCourseRepository;
import com.example.Course.Hub.SERVICES.Student.StudentServices;
import com.example.Course.Hub.SERVICES.Trainer.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;





/**
 * StudentController — handles all student-facing pages and flows.
 *
 * ════════════════════════════════════════════════════════════
 * WHAT'S UPDATED in this version:
 *
 * myCourses()  →  now fetches enrolled courses FROM DB
 *                 (not from session — session was lost on restart)
 *
 * allCourses() →  enrolledIds now comes FROM DB
 *                 (so "Go to Course" button correctly shows for DB-enrolled courses)
 *
 * studentHome() → enrolledCourses now comes FROM DB
 *                 (so "My Courses" section on dashboard shows DB-enrolled courses)
 *
 * courseDetail() → /course/{courseId} — unchanged, works as before
 * watchLesson()  → /lesson/{lessonId} — unchanged, works as before
 * ════════════════════════════════════════════════════════════
 */





@Controller
public class StudentController {



    @Autowired
    TrainerService tService;

    @Autowired
    StudentServices stServices;


    /**
     * NEW: Autowire EnrolledCourseRepository to:
     *   1. Fetch enrolled course IDs for a student from DB
     *   2. Convert IDs → Course objects for display
     */
    @Autowired
    EnrolledCourseRepository enrolledRepo;











    // ════════════════════════════════════════════════════════════
    //  PRIVATE HELPER — get enrolled Course objects for a student
    //
    //  HOW IT WORKS:
    //    1. enrolledRepo.findByStudentEmail(email)
    //       → SELECT * FROM enrolled_course WHERE student_email = ?
    //       → Returns List<EnrolledCourse> (just IDs, not Course objects)
    //
    //    2. For each EnrolledCourse, call tService.getCourse(courseId)
    //       → SELECT * FROM course WHERE course_id = ?
    //       → Returns the full Course object
    //
    //    3. Return as List<Course> — ready for Thymeleaf
    //
    //  WHY NOT @ManyToMany?
    //    Keeping it simple for now. Direct ID → Course lookup is easy
    //    to understand and debug. ManyToMany adds complexity.
    // ════════════════════════════════════════════════════════════
    private List<Course> getEnrolledCoursesFromDB(String studentEmail) {
        List<EnrolledCourse> enrollments = enrolledRepo.findByStudentEmail(studentEmail);

        List<Course> enrolledCourses = new ArrayList<>();
        for (EnrolledCourse enrollment : enrollments) {
            try {
                Course course = tService.getCourse(enrollment.getCourseId());
                if (course != null) {
                    enrolledCourses.add(course);
                }
            } catch (Exception e) {
                // If course was deleted from DB but enrollment remains, skip it
                // This prevents crashes from orphan enrollment records
            }
        }
        return enrolledCourses;
    }

    /**
     * PRIVATE HELPER — get just the enrolled course IDs for a student.
     * Used for button state logic ("Go to Course" vs "Add to Cart").
     */
    private List<String> getEnrolledCourseIdsFromDB(String studentEmail) {
        return enrolledRepo.findByStudentEmail(studentEmail)
                .stream()
                .map(EnrolledCourse::getCourseId)
                .collect(Collectors.toList());
    }

    /**
     * PRIVATE HELPER — safely get student email from session.
     * Returns null if not logged in or session is invalid.
     */
    private String getStudentEmail(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof Users) {
            return ((Users) user).getEmail();
        }
        return null;
    }

















    // ════════════════════════════════════════════════════════════
    //  GET /student-home   →   Student Dashboard
    //  Template: Home/student_Home.html
    //
    //  UPDATED: enrolledCourses now comes FROM DB
    //  Previously: came only from session (lost on server restart)
    //  Now: comes from enrolled_course table → persists correctly
    // ════════════════════════════════════════════════════════════

    @GetMapping("/student-home")
    public String studentHome(HttpSession session, Model model) {

        // 1. Cart count for navbar badge
        //where this function exist give this code to fetch cart count detais
        Object cart = session.getAttribute("cart");
        int cartCount = (cart instanceof List) ? ((List)cart).size() : 0;
        model.addAttribute("cartCount", cartCount);


        // 2. Get enrolled courses (after checkout, store in session)
        //    OR fetch from EnrolledCourse table (future feature)
        //    For now: use session-based enrolled list
        /*Object enrolled = session.getAttribute("enrolledCourses");
        List<Course> enrolledCourses = (enrolled instanceof List) ?
                (List<Course>) enrolled :
                new ArrayList<>();
        model.addAttribute("enrolledCourses", enrolledCourses);*/

        // 2. Enrolled courses — now fetched FROM DB
        String email = getStudentEmail(session);
        List<Course> enrolledCourses = new ArrayList<>();
        if (email != null) {
            enrolledCourses = getEnrolledCoursesFromDB(email);
        }
        model.addAttribute("enrolledCourses", enrolledCourses);


        // 3. All courses for "Recommended" section
        List<Course> allCourses = tService.getAllCourses();
        // Show first 6 as recommendations
        List<Course> recommended = allCourses.size() > 6 ?
                allCourses.subList(0, 6) : allCourses;

        model.addAttribute("courses", recommended);
        model.addAttribute("courseCount", allCourses.size());

        return "Home/student_Home";
    }








// get



    // ════════════════════════════════════════════════════════════
    //  GET /course-details/{courseId}   →   Pre-enrollment course preview
    //  Template: Course/student_course_details.html
    //  UNCHANGED from previous version.
    // ════════════════════════════════════════════════════════════

    @GetMapping("/course-details/{courseId}")
    public String courseDetails(@PathVariable("courseId") String courseId,
                                HttpSession session,
                                Model model) {

        // 1. Fetch the specific course
        Course course = tService.getCourse(courseId);

        // 2. Fetch all courses and filter out the current one
        //    for "Recommended" section (show max 4)
        List<Course> all = tService.getAllCourses();
        List<Course> recommended = all.stream()
                .filter(c -> !c.getCourseId().equals(courseId))
                .limit(4)
                .collect(Collectors.toList());

        // 3. Cart count for navbar badge
        Object cart = session.getAttribute("cart");
        int cartCount = (cart instanceof List) ?
                ((List) cart).size() : 0;

        // 4. Put everything in Model
        model.addAttribute("course", course);
        model.addAttribute("recommended", recommended);
        model.addAttribute("cartCount", cartCount);

        //return "Course/student_course_details";
        return "Test/cou_detail";
    }










    // ════════════════════════════════════════════════════════════
    //  GET /my-courses   →   My Enrolled Courses page
    //  Template: Course/student_enrolled_courses.html
    //
    //  UPDATED: enrolledCourses comes FROM DB
    //  Flow:
    //    1. Get student email from session
    //    2. Query enrolled_course table by email
    //    3. For each EnrolledCourse row, fetch the Course object
    //    4. Pass List<Course> to Thymeleaf
    //
    //  This means:
    //    ✔ Survives server restart (data is in DB, not session)
    //    ✔ Shows courses enrolled from any session (browser, mobile, etc.)
    //    ✔ Free courses enrolled via checkout appear immediately
    // ════════════════════════════════════════════════════════════

    @GetMapping("/my-courses")
    public String myCourses(HttpSession session,
                            Model model,
                            @RequestParam(value = "enrolled", required = false) String enrolled

                                ) {

      // 1. Get enrolled courses from session
      //    (After checkout, CartController saves to session)
     /*Object enrolled = session.getAttribute("enrolledCourses");
      List<Course> enrolledCourses = (enrolled instanceof List) ?
                                     (List<Course>) enrolled :
                                     new ArrayList<>();*/

        String email = getStudentEmail(session);
        List<Course> enrolledCourses = new ArrayList<>();

        if (email != null) {
            /*
             * DB QUERY FLOW:
             *   enrolledRepo.findByStudentEmail("om@gmail.com")
             *   → [{id:1, studentEmail:"om@gmail.com", courseId:"JAVA101", enrolledOn:2026-04-18}, ...]
             *
             *   Then for each: tService.getCourse("JAVA101")
             *   → Full Course object with name, trainer, lessons, etc.
             */
            enrolledCourses = getEnrolledCoursesFromDB(email);
        }

      // 2. Cart count for navbar
      Object cart = session.getAttribute("cart");
      int cartCount = (cart instanceof List) ? ((List)cart).size() : 0;
      model.addAttribute("cartCount", cartCount);

      model.addAttribute("enrolledSuccess", "true".equals(enrolled));

      model.addAttribute("enrolledCourses", enrolledCourses);
      return "Course/student_enrolled_courses";
  }
















    // ════════════════════════════════════════════════════════════
    //  GET /all-courses   →   Browse all courses (logged-in student)
    //  Template: Course/student_all_courses.html
    //
    //  UPDATED: enrolledIds now comes FROM DB
    //  This makes the "Go to Course" button show correctly
    //  even after server restart or new login session.
    // ════════════════════════════════════════════════════════════

    @GetMapping("/all-courses")
    public String allCourses(HttpSession session, Model model) {

        // 1. All courses for display
        List<Course> courses = tService.getAllCourses();
        model.addAttribute("courses", courses);


        // 2. Cart — read from session for "already in cart" logic
        Object cartObj = session.getAttribute("cart");
        List<Course> cart = (cartObj instanceof List)
                ? (List<Course>) cartObj : new ArrayList<>();



        // 2. Cart course IDs (to check "already in cart")
        //List<Course> cart = getCart(session);

        // 3. Set of course IDs already in cart
        //    Used in Thymeleaf to show "Already in Cart" button
        List<String> cartIds = cart.stream()
                .map(Course::getCourseId).collect(Collectors.toList());
        model.addAttribute("cartIds", cartIds);

        // 3. Enrolled course IDs (to check "already purchased")
        /*Object enrolled = session.getAttribute("enrolledCourses");
        List<Course> enrolledList = (enrolled instanceof List) ?
                (List<Course>) enrolled :
                new ArrayList<>();
        List<String> enrolledIds = enrolledList.stream()
                .map(Course::getCourseId).collect(Collectors.toList());
        model.addAttribute("enrolledIds", enrolledIds);*/



        // Enrolled IDs — NOW FROM DB (not session)
        String email = getStudentEmail(session);
        List<String> enrolledIds = new ArrayList<>();
        if (email != null) {
            enrolledIds = getEnrolledCourseIdsFromDB(email);
        }
        model.addAttribute("enrolledIds", enrolledIds);



        // 4. Cart count for navbar
        model.addAttribute("cartCount", cart.size());

        return "Course/student_all_courses";
    }



















    // ════════════════════════════════════════════════════════════
    //  GET /course/{courseId}   →   Course Detail + Grouped Lessons
    //  Template: Course/student_course_lesson_details.html
    //
    //  UNCHANGED from previous version.
    //  Shows: full course info + lessons grouped by topic.
    //
    //  HOW GROUPING WORKS:
    //    Sort lessons by lessonId → then group by lessonTopic
    //    into a LinkedHashMap<String, List<Lesson>>
    //
    //  THYMELEAF:
    //    th:each="entry : ${topics}"
    //      entry.key   → topic name
    //      entry.value → List<Lesson> for this topic
    // ════════════════════════════════════════════════════════════
    @GetMapping("/course/{courseId}")
    public String student_course_Lesson_Detail(@PathVariable("courseId") String courseId,
                               HttpSession session,
                               Model model) {

        // Fetch course from DB — lessons loaded via @OneToMany(mappedBy="course")
        Course course = tService.getCourse(courseId);

        // Sort lessons by ID, then group by topic
        List<Lesson> sortedLessons = course.getLessons().stream()
                .sorted(Comparator.comparingInt(Lesson::getLessonId))
                .collect(Collectors.toList());

        // LinkedHashMap preserves insertion order → topics appear in lesson order
        Map<String, List<Lesson>> topics = new LinkedHashMap<>();
        for (Lesson lesson : sortedLessons) {
            String topic = (lesson.getLessonTopic() != null && !lesson.getLessonTopic().isBlank())
                    ? lesson.getLessonTopic()
                    : "General";
            topics.computeIfAbsent(topic, k -> new ArrayList<>()).add(lesson);
        }

        Object cart = session.getAttribute("cart");
        int cartCount = (cart instanceof List) ? ((List<?>) cart).size() : 0;

        model.addAttribute("course", course);
        model.addAttribute("topics", topics);
        model.addAttribute("topicCount", topics.size());
        model.addAttribute("cartCount", cartCount);

        return "Course/student_course_lesson_details";

    }















    // ════════════════════════════════════════════════════════════
    //  GET /lesson/{lessonId}   →   Watch lesson + Video player
    //  Template: Course/student_my_lessons.html
    //
    //  UNCHANGED from previous version.
    //  Shows: video player (YouTube/Drive auto-detect) + lesson info + comments.
    //
    //  NOTE on LazyInitializationException:
    //    lesson.getCourse() uses LAZY fetch.
    //    If you get LazyInitializationException, add @Transactional to this method.
    //    Or change Lesson.course to FetchType.EAGER temporarily for debugging.
    // ════════════════════════════════════════════════════════════


    // ════════════════════════════════════════════════════════════
    //  GET /lesson/{courseId}/{lessonId}  →  Watch Lesson Page
    //
    //  URL CHANGE:
    //    OLD: /lesson/{lessonId}          ← BROKEN (ambiguous lessonId)
    //    NEW: /lesson/{courseId}/{lessonId} ← FIXED (unique combination)
    //
    //  PERFORMANCE:
    //    OLD: nested loop over all courses + all their lessons = O(n*m)
    //    NEW: single DB query via findByCourse_CourseIdAndLessonId = O(1)
    //
    //  MODEL:
    //    lesson        → the specific Lesson object
    //    course        → the parent Course (fetched by courseId)
    //    videoUrl      → plain String (no lazy-load risk)
    //    courseLessons → all lessons of this course (sorted) for sidebar
    //    cartCount     → int for navbar badge
    // ════════════════════════════════════════════════════════════
    @GetMapping("/lesson/{courseId}/{lessonId}")
    public String watchLesson(@PathVariable("courseId") String courseId,
                              @PathVariable("lessonId") int lessonId,
                              HttpSession session,
                              Model model) {

        // Step 1: Fetch Course directly by courseId — single query, always fast
        Course course = tService.getCourse(courseId);

        // Step 2: Fetch the exact lesson using BOTH courseId AND lessonId
        //   This calls: LessonRepository.findByCourse_CourseIdAndLessonId(courseId, lessonId)
        //   SQL: SELECT * FROM lesson WHERE course_id = (SELECT id FROM course WHERE course_id = ?)
        //        AND lesson_id = ?
        Lesson lesson = stServices.getLessonByCourseAndId(courseId, lessonId);

        if (lesson == null || course == null) {
            // Lesson or course not found — redirect to my-courses
            return "redirect:/my-courses";
        }

        // Step 3: Extract videoUrl as a plain String — prevents lazy-load issues in Thymeleaf
        String videoUrl = (lesson.getLessonVideoLink() != null)
                ? lesson.getLessonVideoLink().trim() : "";

        // Step 4: Fetch ALL lessons for this course for sidebar navigation
        //   Uses the efficient direct query instead of loading Course.getLessons()
        List<Lesson> courseLessons = stServices.getLessonsByCourse(courseId);

        // Step 5: cart count for navbar
        Object cart = session.getAttribute("cart");
        int cartCount = (cart instanceof List) ? ((List<?>) cart).size() : 0;

        // Step 6: Pass all as SEPARATE fully-loaded model attributes
        model.addAttribute("lesson", lesson);
        model.addAttribute("course", course);
        model.addAttribute("videoUrl", videoUrl);
        model.addAttribute("courseLessons", courseLessons);
        model.addAttribute("cartCount", cartCount);

        return "Course/student_my_lessons";

    }


    // ════════════════════════════════════════════════════════════
    //  KEEP the old /lesson/{lessonId} route as a redirect for
    //  backward compatibility (e.g., bookmarked URLs).
    //  It redirects to the new format using the first course that has
    //  this lessonId — not perfect, but prevents 404s.
    //
    //  You can REMOVE this if you're sure no old URLs are bookmarked.
    // ════════════════════════════════════════════════════════════
    @GetMapping("/lesson/{lessonId}")
    public String watchLessonLegacy(@PathVariable("lessonId") int lessonId) {
        // Legacy URL — can't resolve without courseId, redirect to my-courses
        return "redirect:/my-courses";
    }



















    @PostMapping("/gotoLesson")
      public String myLesson(@RequestParam("lessonId") int lessonId){
       Lesson lesson = stServices.getLessonId(lessonId);
       return "lesson";
      }





















 // ════════════════════════════════════════════════════════════
    //  GET /student-home   →   Student Dashboard

    














}
