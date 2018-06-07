package com.tpgrade.models;

import com.orm.SugarRecord;

import java.util.Date;

public class UserAnswer extends SugarRecord {
    public User user;
    public Date created;
    private int answer;

    // Default constructor is necessary for SugarRecord
    public UserAnswer() {

    }

    public UserAnswer(int answer, User user) {
        this.answer = answer;
        this.user = user;
        this.created = new Date();
    }
}

