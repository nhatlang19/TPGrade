package com.tpgrade.tpgrade.Processors.Detect.Conners;

import java.util.ArrayList;
import java.util.List;

public class ListDetectRectangle {
    List<DetectRectangle> list;

    public ListDetectRectangle() {
        list = new ArrayList<>();
    }

    public void add(DetectRectangle rect) {
        list.add(rect);
    }

    public void detect() {
        for(DetectRectangle rectangle : list) {
            rectangle.detect();
        }
    }
}
