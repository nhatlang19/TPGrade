package com.tpgrade.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User extends SugarRecord {
    private String info;
    private String answerImage;
    public Date created;
    public List<UserAnswer> answers;

    // Default constructor is necessary for SugarRecord
    public User() {
        this.answers = new ArrayList<>();
    }

    public User(String info, String answerImage) {
        this.info = info;
        this.answerImage = answerImage;
        this.created = new Date();
        this.answers = new ArrayList<>();
    }
}

