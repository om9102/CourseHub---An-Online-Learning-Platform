package com.example.Course.Hub.Controller;


import com.example.Course.Hub.ENTITY.Course;
import com.example.Course.Hub.ENTITY.EnrolledCourse;
import com.example.Course.Hub.ENTITY.Users;
import com.example.Course.Hub.REPOSITORY.EnrolledCourseRepository;
import com.example.Course.Hub.SERVICES.Trainer.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    TrainerService tService;


    @Autowired
    EnrolledCourseRepository enrolledRepo;



    // ── VIEW CART ──────────────────────────────────
    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<Course> cart = getCart(session);


        //Correct total — only sum paid courses, skip free ones
        int total = 0;
        for (Course c : cart) {
            if (!"free".equalsIgnoreCase(c.getCourseType())) {
                try { total += Integer.parseInt(c.getCoursePrice()); }
                catch (NumberFormatException e) {/* skip if not numeric */ }
            }
        }



        // --- CART IDs (for recommended "In Cart" badge) ---
        // Extract just the courseId strings from the cart list
        // Result example: ["JAVA101", "PY201"]
        List<String> cartIds = cart.stream()
                .map(Course::getCourseId)
                .collect(Collectors.toList());
        model.addAttribute("cartIds", cartIds);



        // --- RECOMMENDED COURSES (FIX 3) ---
        // Get all courses from DB, filter out those already in cart, show max 4
        List<Course> allCourses = tService.getAllCourses();
        List<Course> recommended = allCourses.stream()
                .filter(c -> !cartIds.contains(c.getCourseId()))  // exclude courses already in cart
                .limit(4)                                          // max 4 recommendations
                .collect(Collectors.toList());
        model.addAttribute("recommended", recommended);



        model.addAttribute("cart", cart);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartCount", cart.size());


        return "Home/cart";
        //return "Test/car_chrome";
    }











    // ── ADD TO CART ────────────────────────────────
    @GetMapping("/add")
    public String addToCart(@RequestParam("courseId") String courseId,
                            HttpSession session) {
        List<Course> cart = getCart(session);


        // Check if already in cart (avoid duplicate in cart list)
        boolean alreadyIn = cart.stream()
                .anyMatch(c -> c.getCourseId().equals(courseId));


        // Check if already enrolled in DB (no need to add to cart)
        Users user = (Users) session.getAttribute("user");
        boolean alreadyEnrolled = false;
        if (user != null) {
            alreadyEnrolled = enrolledRepo.existsByStudentEmailAndCourseId(
                    user.getEmail(), courseId);
        }


        if (!alreadyIn) {
            Course course = tService.getCourse(courseId);
            if (course != null) cart.add(course);
        }



        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", cart.size());
        //return "redirect:/cart";
        return "redirect:/all-courses";
    }








    // ── REMOVE FROM CART ─────────────────────────
    //  GET /cart/remove?courseId=XXX   →   Remove course from cart
    @GetMapping("/remove")
    public String removeFromCart(@RequestParam("courseId") String id,
                                 HttpSession session) {
        List<Course> cart = getCart(session);
        cart.removeIf(c -> c.getCourseId().equals(id));
        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", cart.size());
        return "redirect:/cart";
    }







    // ── CHECKOUT ─────────────────────────────────

    // ════════════════════════════════════════════════════════════
    //  GET /cart/checkout   →   Process cart and enroll free courses
    //
    //  ┌─────────────────────────────────────────────────────┐
    //  │  FULL CHECKOUT LOGIC                                │
    //  │                                                     │
    //  │  FOR each course in cart:                           │
    //  │    IF courseType == "free":                         │
    //  │      Check if already enrolled in DB               │
    //  │      IF not enrolled:                               │
    //  │        Save EnrolledCourse to DB                    │
    //  │    ELSE (paid):                                     │
    //  │      Skip for now — payment integration later      │
    //  │                                                     │
    //  │  AFTER processing all courses:                      │
    //  │    Clear cart from session                          │
    //  │    Update session cartCount = 0                     │
    //  │    Redirect to /my-courses                          │
    //  └─────────────────────────────────────────────────────┘
    //
    //  EDGE CASES HANDLED:
    //    - Empty cart → just clears and redirects (no crash)
    //    - Mixed cart (free + paid) → enrolls free, skips paid
    //    - Duplicate course in cart → DB check prevents double-save
    //    - Already enrolled course → DB check prevents double-save
    //    - User not in session → clears cart, redirects safely
    // ════════════════════════════════════════════════════════


    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        List<Course> cart = getCart(session);
        // TODO: save enrollment records to DB for each course
        // Enrollment entity + EnrollmentRepository needed here



        // Get logged-in student from session
        Users student = (Users) session.getAttribute("user");

        // Safety check: if no user in session, just clear and redirect
        if (student == null) {
            session.removeAttribute("cart");
            session.setAttribute("cartCount", 0);
            return "redirect:/login";
        }

        String studentEmail = student.getEmail();

        // ── PROCESS EACH COURSE IN CART ──────────────────────────
        for (Course course : cart) {

            String courseType = course.getCourseType();

            if ("free".equalsIgnoreCase(courseType)) {
                /*
                 * FREE COURSE → Enroll directly in DB
                 *
                 * Before saving, check if already enrolled.
                 * existsByStudentEmailAndCourseId() runs:
                 *   SELECT COUNT(*) FROM enrolled_course
                 *   WHERE student_email = ? AND course_id = ?
                 * Returns true if found → skip to avoid duplicate.
                 */
                boolean alreadyEnrolled = enrolledRepo.existsByStudentEmailAndCourseId(
                        studentEmail, course.getCourseId());

                if (!alreadyEnrolled) {
                    // Create new enrollment record and save to DB
                    EnrolledCourse enrollment = new EnrolledCourse(
                            studentEmail,
                            course.getCourseId(),
                            course.getCourseType()
                    );
                    enrolledRepo.save(enrollment);
                    // enrolledOn is set automatically in EnrolledCourse constructor
                }
                // If alreadyEnrolled == true → do nothing (skip silently)

            } else {
                /*
                 * PAID COURSE → Skip for now.
                 * Payment integration will go here later.
                 *
                 * Future code structure:
                 *   if (paymentSuccess) {
                 *       EnrolledCourse ec = new EnrolledCourse(email, courseId, "paid");
                 *       enrolledRepo.save(ec);
                 *   }
                 */
                // TODO: Payment integration — paid courses not enrolled yet
            }
        }





        // ── CLEAR CART FROM SESSION
        session.removeAttribute("cart");
        session.setAttribute("cartCount", 0);
        model.addAttribute("message", "Payment successful!");


        // Redirect to My Courses so student can see their new enrollments
        //return "redirect:/my-courses";


        //return "redirect:/student-home";   // Go to student home page after payment

        return "redirect:/my-courses?enrolled=true";
    }










    // ── HELPER: get or create cart from session ─────────
    // ════════════════════════════════════════════════════════════
    //  HELPER: get or create cart list from session
    //
    //  session.getAttribute("cart") returns Object.
    //  We cast to List<Course> safely.
    //  If null or wrong type → return empty ArrayList.
    // ════════════════════════════════════════════════════════════
    public static List<Course> getCart(HttpSession session) {
        Object obj = session.getAttribute("cart");
        if (obj instanceof List) {
            return (List<Course>) obj;
        }
        return new ArrayList<>();
    }







    // ════════════════════════════════════════════════════════════
    //  HELPER: get enrolled courses from session (legacy support)
    //  Still used in some places as fallback.
    //  The main source of truth is now the DB via enrolledRepo.
    // ════════════════════════════════════════════════════════════
    public static List<Course> getEnrolled(HttpSession session) {
        Object obj = session.getAttribute("enrolledCourses");
        if (obj instanceof List) {
            //noinspection unchecked
            return (List<Course>) obj;
        }
        return new ArrayList<>();
    }




}