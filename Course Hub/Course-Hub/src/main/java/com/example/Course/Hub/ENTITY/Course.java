package com.example.Course.Hub.ENTITY;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Course {



    /**
     * Course entity.
     *
     * ════════════════════════════════════════════════════════════
     * BUG FIX: @OneToMany without mappedBy creates a JOIN TABLE
     * ════════════════════════════════════════════════════════════
     * ORIGINAL CODE (causes a "course_lessons" join table):
     *   @OneToMany
     *   List<Lesson> lessons;
     *
     * FIXED CODE:
     *   @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     *   List<Lesson> lessons;
     *
     * WHY THIS MATTERS:
     *   Without mappedBy, JPA creates a third join table (course_lessons).
     *   With mappedBy = "course", JPA uses the existing foreign key column
     *   in the Lesson table (course_courseId) — no extra join table.
     *
     * ALSO: cascade = CascadeType.ALL means saving a Course also saves its lessons.
     *       fetch = FetchType.LAZY means lessons are NOT loaded unless accessed.
     *       This is important for performance — don't load all lessons when you
     *       just need the course name.
     *
     * NOTE: For the /course/{courseId} page, we call course.getLessons() so JPA
     * will trigger a lazy load at that point. This is fine since we need the data.
     *
     * ALSO FIX in Lesson.java:
     *   @ManyToOne(fetch = FetchType.LAZY)
     *   @JoinColumn(name = "course_id")  ← explicit column name
     *   Course course;
     * ════════════════════════════════════════════════════════════
     */











    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String courseId;
    String courseName;
    String courseShortDescription;
    String trainerName;
    String coursePrice;
    String courseType;




    /*
     * CORRECTED MAPPING:
     *   mappedBy = "course" → refers to the 'course' field in Lesson.java
     *   cascade = ALL       → save/delete course also affects its lessons
     *   fetch = LAZY        → don't load lessons unless we ask for them
     */


    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Lesson> lessons;







    //Constructors
    public Course() {
    }


    public Course(int id, String courseId, String courseName, String courseShortDescription, String trainerName, String coursePrice, String courseType, List<Lesson> lessons) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseShortDescription = courseShortDescription;
        this.trainerName = trainerName;
        this.coursePrice = coursePrice;
        this.courseType = courseType;
        this.lessons = lessons;
    }




    //Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseShortDescription() {
        return courseShortDescription;
    }

    public void setCourseShortDescription(String courseShortDescription) {
        this.courseShortDescription = courseShortDescription;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(String coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }


    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseShortDescription='" + courseShortDescription + '\'' +
                ", trainerName='" + trainerName + '\'' +
                ", coursePrice='" + coursePrice + '\'' +
                ", courseType='" + courseType + '\'' +
                ", lessons=" + lessons +
                '}';
    }
}



