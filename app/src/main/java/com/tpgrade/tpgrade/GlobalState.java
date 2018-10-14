package com.tpgrade.tpgrade;

import com.orm.SugarApp;

import java.util.Arrays;
import java.util.List;

public class GlobalState extends SugarApp {
    private long selectedTopicId;

    private static List<Integer> rect = Arrays.asList(0, 0, 0, 0);

    public long getSelectedTopicId() {
        return selectedTopicId;
    }

    public void setSelectedTopicId(long selectedTopicId) {
        this.selectedTopicId = selectedTopicId;
    }

    public static boolean updateRect(int i, int value) {
        rect.set(i, value);

        System.out.println(rect.get(0) + ":" + rect.get(1) + ":" + rect.get(2) + ":" + rect.get(3));
        return rect.get(0) != 0
                && rect.get(1) != 0
                && rect.get(2) != 0
                && rect.get(3) != 0;
    }

    public static boolean isValid() {
        return rect.get(0) != 0
                && rect.get(1) != 0
                && rect.get(2) != 0
                && rect.get(3) != 0;
    }
}
