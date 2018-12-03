package com.tpgrade.tpgrade.Processors;

import org.opencv.core.MatOfPoint;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public List<MatOfPoint> cnts = new ArrayList<>();

    public void add(MatOfPoint point) {
        cnts.add(point);
    }
}
