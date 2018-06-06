package com.tpgrade.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exam extends SugarRecord {
    public String examTitle; // 012, 112, 312
    public Date created;

    public Topic topic;
    public List<ExamAnswer> answers;

    // Default constructor is necessary for SugarRecord
    public Exam() {
        this.answers = new ArrayList<>();
    }

    public Exam(String examTitle, Topic topic) {
        this.topic = topic;
        this.examTitle = examTitle;
        this.created = new Date();
        this.answers = new ArrayList<>();
    }
}

