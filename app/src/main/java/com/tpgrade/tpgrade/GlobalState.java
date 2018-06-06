package com.tpgrade.tpgrade;

import com.orm.SugarApp;

public class GlobalState extends SugarApp {
    private long selectedTopicId;

    public long getSelectedTopicId() {
        return selectedTopicId;
    }

    public void setSelectedTopicId(long selectedTopicId) {
        this.selectedTopicId = selectedTopicId;
    }
}
