package com.example.Course.Hub.ENTITY;


import jakarta.persistence.*;

@Entity

public class Lesson {




    /**
     * Lesson entity.
     *
     * ════════════════════════════════════════════════════════════
     * FIX: Add @JoinColumn to ManyToOne for explicit FK column name
     * ════════════════════════════════════════════════════════════
     * ORIGINAL:
     *   @ManyToOne
     *   Course course;
     *
     * FIXED:
     *   @ManyToOne(fetch = FetchType.LAZY)
     *   @JoinColumn(name = "course_id")
     *   Course course;
     *
     * WHY:
     *   @JoinColumn(name = "course_id") creates a column named "course_id"
     *   in the lesson table that holds the FK reference to course.courseId.
     *   This matches what Course.mappedBy="course" expects.
     *
     *   FetchType.LAZY: don't load the parent Course every time we load
     *   a Lesson. Only load when lesson.getCourse() is called.
     *
     * NOTE: When calling lesson.getCourse() in student_my_lessons.html,
     *   JPA will lazy-load the Course. This works fine within a transaction.
     *   If you get LazyInitializationException, add @Transactional to the
     *   controller method, OR use FetchType.EAGER on this @ManyToOne.
     * ════════════════════════════════════════════════════════════
     */





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;   // Primary key (AUTO)

    int lessonId;
    String lessonName;
    String lessonTopic;
    String lessonDescription;
    String lessonVideoLink;


    /*
     * CORRECTED MAPPING:
     *   fetch = LAZY        → don't load Course unless lesson.getCourse() called
     *   @JoinColumn         → explicit FK column name in DB
     */


    @ManyToOne(fetch = FetchType.LAZY)
    //@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    Course course;


    public Lesson() {
    }


    public Lesson( int lessonId, String lessonName, String lessonTopic, String lessonDescription, String lessonVideoLink, Course course) {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.lessonTopic = lessonTopic;
        this.lessonDescription = lessonDescription;
        this.lessonVideoLink = lessonVideoLink;
        this.course = course;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonTopic() {
        return lessonTopic;
    }

    public void setLessonTopic(String lessonTopic) {
        this.lessonTopic = lessonTopic;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    public String getLessonVideoLink() {
        return lessonVideoLink;
    }

    public void setLessonVideoLink(String lessonVideoLink) {
        this.lessonVideoLink = lessonVideoLink;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }


    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", lessonId=" + lessonId +
                ", lessonName='" + lessonName + '\'' +
                ", lessonTopic='" + lessonTopic + '\'' +
                ", lessonDescription='" + lessonDescription + '\'' +
                ", lessonVideoLink='" + lessonVideoLink + '\'' +
                ", course=" + course +
                '}';
    }
}
