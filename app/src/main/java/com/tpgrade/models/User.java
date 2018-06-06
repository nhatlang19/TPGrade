package com.tpgrade.models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User extends SugarRecord {
    private String info;
    public Date created;
    public List<UserAnswer> answers;

    // Default constructor is necessary for SugarRecord
    public User() {
        this.answers = new ArrayList<>();
    }

    public User(String info) {
        this.info = info;
        this.created = new Date();
        this.answers = new ArrayList<>();
    }
}

