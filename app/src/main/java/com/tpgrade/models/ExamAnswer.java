package com.tpgrade.models;

import com.orm.SugarRecord;

import java.util.Date;

public class ExamAnswer extends SugarRecord {
    public Exam exam;
    public Date created;
    private int answer;

    // Default constructor is necessary for SugarRecord
    public ExamAnswer() {

    }

    public ExamAnswer(int answer, Exam exam) {
        this.answer = answer;
        this.exam = exam;
        this.created = new Date();
    }
}

