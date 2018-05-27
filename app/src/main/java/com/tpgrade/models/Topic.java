package com.tpgrade.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.Serializable;
import java.util.Date;

public class Topic extends SugarRecord implements Serializable {
    public String testName;
    public int typePaper;
    public int numbers;
    public int topScore;
    public Date created;
    public int answerNumber;
    public int keyNumber;
    public float averageScore;
    public float minScore;
    public float maxScore;

    @Ignore
    public int position;


    // Default constructor is necessary for SugarRecord
    public Topic() {

    }

    public Topic(String testName, int typePaper, int numbers, int topScore) {
        this.testName = testName;
        this.typePaper = typePaper;
        this.numbers = numbers;
        this.topScore = topScore;
        this.created = new Date();
    }
}
