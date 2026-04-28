package com.example.Course.Hub.ENTITY;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "enrolled_course")
public class EnrolledCourse {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;




    /**
     * The email of the student who enrolled.
     * Sourced from: session.getAttribute("user").getEmail()
     * This ties the enrollment to a specific student account.
     */
    @Column(name = "student_email", nullable = false)
    private String studentEmail;







    /**
     * The courseId of the enrolled course.
     * Matches Course.courseId (the @Id of the Course entity).
     * We store just the ID here (not a full FK join) to keep it simple.
     * To fetch the Course: tService.getCourse(this.courseId)
     */
    @Column(name = "course_id", nullable = false)
    private String courseId;






    /**
     * Date of enrollment — set automatically at checkout time.
     * Useful for showing "enrolled on" in the UI later.
     */
    @Column(name = "enrolled_on")
    private LocalDate enrolledOn;





    /**
     * Course type at time of enrollment — "free" or "paid".
     * Stored here so we know how the enrollment happened.
     */
    @Column(name = "course_type")
    private String courseType;






    public EnrolledCourse() { }

    /**
     * Constructor used in CartController.checkout() when enrolling a free course.
     *
     * Usage:
     *   EnrolledCourse ec = new EnrolledCourse(
     *       student.getEmail(),
     *       course.getCourseId(),
     *       course.getCourseType()
     *   );
     *   enrolledRepo.save(ec);
     */
    public EnrolledCourse(String studentEmail, String courseId, String courseType) {
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.courseType = courseType;
        this.enrolledOn = LocalDate.now(); // auto-set on creation
    }






    // ── Getters & Setters ──

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public LocalDate getEnrolledOn() { return enrolledOn; }
    public void setEnrolledOn(LocalDate enrolledOn) { this.enrolledOn = enrolledOn; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }




    @Override
    public String toString() {
        return "EnrolledCourse{id=" + id +
                ", studentEmail='" + studentEmail + '\'' +
                ", courseId='" + courseId + '\'' +
                ", enrolledOn=" + enrolledOn +
                ", courseType='" + courseType + '\'' + '}';
    }


}
