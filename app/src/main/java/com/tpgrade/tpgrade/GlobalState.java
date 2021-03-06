package com.tpgrade.tpgrade;

import com.orm.SugarApp;

import org.opencv.core.Point;

import java.util.Arrays;
import java.util.List;

public class GlobalState extends SugarApp {
    private static List<Integer> rect = Arrays.asList(0, 0, 0, 0);
    private static List<Point> rectPoint = Arrays.asList(new Point(), new Point(), new Point(), new Point());
    private long selectedTopicId;

    public static void updateRect(int i, int value, Point point) {
        rect.set(i, value);
        rectPoint.set(i, point);
    }

    public static void resetRect() {
        rect = Arrays.asList(0, 0, 0, 0);
        rectPoint = Arrays.asList(new Point(), new Point(), new Point(), new Point());
    }

    public static boolean isValid() {
        System.out.println("RECT: " + rect.get(0) + ":" + rect.get(1) + ":" + rect.get(2) + ":" + rect.get(3));
        return rect.get(0) != 0
                && rect.get(1) != 0
                && rect.get(2) != 0
                && rect.get(3) != 0;
    }

    public static List<Point> getRectPoint() {
        return rectPoint;
    }

    public static Point[] getRectPointArray() {
        return rectPoint.toArray(new Point[rectPoint.size()]);
    }

    public long getSelectedTopicId() {
        return selectedTopicId;
    }

    public void setSelectedTopicId(long selectedTopicId) {
        this.selectedTopicId = selectedTopicId;
    }
}
