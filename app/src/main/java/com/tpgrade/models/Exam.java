package com.tpgrade.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Exam extends SugarRecord {
    public String examTitle; // 0123, 1123, 3123
    public Date created;

    public Topic topic;
    public String answerStr;

    @Ignore
    public List<String> answers;

    // Default constructor is necessary for SugarRecord
    public Exam() {
        this.topic = new Topic();
        this.examTitle = "";
        this.created = new Date();
        this.answerStr = "";
        this.answers = new ArrayList<>();
    }

    public Exam(String examTitle, Topic topic) {
        this.topic = topic;
        this.examTitle = examTitle;
        this.created = new Date();
        this.answers = new ArrayList<>();
        this.answerStr = "";
    }

    public Exam(String examTitle, Topic topic, String[] answers) {
        this.topic = topic;
        this.examTitle = examTitle;
        this.created = new Date();
        this.answers = new ArrayList<>(Arrays.asList(answers));
        this.answerStr = "";
    }

    @Override
    public long save() {
        this.answerStr = new Gson().toJson(this.answers);
        return super.save();
    }

    public List<String> getAnswers() {
        return new Gson().fromJson(this.answerStr, new TypeToken<List<String>>() {
        }.getType());
    }
}

