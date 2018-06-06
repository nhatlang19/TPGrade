package com.tpgrade.models;

import com.orm.SugarRecord;
import java.util.Date;

public class UserAnswer extends SugarRecord {
    public User user;
    private int answer;
    public Date created;

    // Default constructor is necessary for SugarRecord
    public UserAnswer() {

    }

    public UserAnswer(int answer, User user) {
        this.answer = answer;
        this.user = user;
        this.created = new Date();
    }
}

