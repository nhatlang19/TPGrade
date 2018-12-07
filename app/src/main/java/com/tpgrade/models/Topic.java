package com.tpgrade.models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Date;

public class Topic extends SugarRecord {
    public String testName;         // tên bài
    public int typePaper;           // loại phiếu
    public int numbers;             // số câu
    public int topScore;            // hệ điểm
    public Date created;
    public int answerNumber;
    public int keyNumber;
    public float averageScore;
    public float minScore;
    public float maxScore;

    @Ignore
    public int topicId;
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
