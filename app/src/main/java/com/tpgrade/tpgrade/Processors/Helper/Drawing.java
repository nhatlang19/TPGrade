package com.tpgrade.tpgrade.Processors.Helper;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Drawing {
    public static void drawRect(Mat im, MatOfPoint contour) {
        Drawing.drawColor(im, contour, new Scalar(255, 0, 0, 255), 3);
    }

    public static void drawRectPink(Mat im, MatOfPoint contour) {
        Drawing.drawColor(im, contour, new Scalar(255, 0, 255, 255), 2);
    }

    public static void drawRectBlue(Mat im, MatOfPoint contour) {
        Drawing.drawColor(im, contour, new Scalar(0, 0, 255, 255), 3);
    }

    public static void drawRectGreen(Mat im, MatOfPoint contour) {
        Drawing.drawColor(im, contour, new Scalar(0, 255, 0, 255), 3);
    }

    public static void drawColor(Mat im, MatOfPoint contour, Scalar color, int thickness) {
        Rect rect = Imgproc.boundingRect(contour);
        Point start = new Point(rect.x, rect.y);
        Point end = new Point(rect.x + rect.width, rect.y + rect.height);
        Imgproc.rectangle(im, start, end, color, thickness);
    }
}
