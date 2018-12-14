package com.tpgrade.Lib;

import org.opencv.core.Point;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Helper {
    public static Point min(List<Point> points) {

        Point p1 = points.get(0);
        Point p2 = points.get(1);
        Point p3 = points.get(2);
        Point p4 = points.get(3);

        Double[] numbersX = { p1.x, p2.x, p3.x, p4.x};
        Double minX = (Double) Collections.min(Arrays.asList(numbersX));

        Double[] numbersY = { p1.y, p2.y, p3.y, p4.y };
        Double minY = (Double) Collections.min(Arrays.asList(numbersY));

        return new Point(minX, minY);
    }

    public static Point max(List<Point> points) {

        Point p1 = points.get(0);
        Point p2 = points.get(1);
        Point p3 = points.get(2);
        Point p4 = points.get(3);

        Double[] numbersX = { p1.x, p2.x, p3.x, p4.x};
        Double maxX = (Double) Collections.max(Arrays.asList(numbersX));

        Double[] numbersY = { p1.y, p2.y, p3.y, p4.y };
        Double maxY = (Double) Collections.max(Arrays.asList(numbersY));

        return new Point(maxX, maxY);
    }
}
