package com.tpgrade.tpgrade.Processors.Helper;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sort {
    public static List<MatOfPoint> doSort(List<MatOfPoint> contours) {
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = Double.compare(rect1.tl().y, rect2.tl().y);
                return result;
            }
        });

        // sort by x coordinates
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = 0;
                double total = rect1.tl().y / rect2.tl().y;
                if (total >= 0.9 && total <= 1.1) {
                    result = Double.compare(rect1.tl().x, rect2.tl().x);
                }
                return result;
            }
        });

        return contours;
    }

    public static List<MatOfPoint> doSort2(List<MatOfPoint> contours) {
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = Double.compare(rect1.tl().y, rect2.tl().y);
                return result;
            }
        });

        // sort by x coordinates
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = 0;
                double total = rect1.tl().y / rect2.tl().y;
                if (total >= 0.9 && total <= 1.4) {
                    result = Double.compare(rect1.tl().x, rect2.tl().x);
                }
                return result;
            }
        });

        return contours;
    }
}
