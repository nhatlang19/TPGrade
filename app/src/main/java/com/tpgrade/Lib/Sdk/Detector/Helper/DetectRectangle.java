package com.tpgrade.Lib.Sdk.Detector.Helper;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetectRectangle {

    private Mat dst;
    private Mat gray;

    private Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG;
    private MatOfPoint2f approxCurve;

    private int threshold;

    public DetectRectangle(Mat dst, Mat gray) {
        this.dst = dst;
        this.gray = gray;

        bwIMG = new Mat();
        dsIMG = new Mat();
        hsvIMG = new Mat();
        lrrIMG = new Mat();
        urrIMG = new Mat();
        usIMG = new Mat();
        cIMG = new Mat();
        hovIMG = new Mat();
        approxCurve = new MatOfPoint2f();

        //initialize treshold
        threshold = 100;
    }

    public synchronized Mat detect() {
        Imgproc.pyrDown(gray, dsIMG, new Size(gray.cols() / 2, gray.rows() / 2));
        Imgproc.pyrUp(dsIMG, usIMG, gray.size());

        Imgproc.Canny(usIMG, bwIMG, 0, threshold);

        Imgproc.dilate(bwIMG, bwIMG, new Mat(), new Point(-1, 1), 1);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        cIMG = bwIMG.clone();

        Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);


        for (MatOfPoint cnt : contours) {

            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());

            Imgproc.approxPolyDP(curve, approxCurve, 0.02 * Imgproc.arcLength(curve, true), true);

            int numberVertices = (int) approxCurve.total();

            double contourArea = Imgproc.contourArea(cnt);

            if (Math.abs(contourArea) < 100) {
                continue;
            }

            //Rectangle detected
            if (numberVertices >= 4 && numberVertices <= 6) {

                List<Double> cos = new ArrayList<>();

                for (int j = 2; j < numberVertices + 1; j++) {
                    cos.add(angle(approxCurve.toArray()[j % numberVertices], approxCurve.toArray()[j - 2], approxCurve.toArray()[j - 1]));
                }

                Collections.sort(cos);

                double mincos = cos.get(0);
                double maxcos = cos.get(cos.size() - 1);

                if (numberVertices == 4 && mincos >= -0.1 && maxcos <= 0.3) {
                    setLabel(dst, "X", cnt);
                }

            }
        }

        return dst;
    }

    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private void setLabel(Mat im, String label, MatOfPoint contour) {
        int fontface = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 3;//0.4;
        int thickness = 3;//1;
        int[] baseline = new int[1];
        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);
        Point pt = new Point(r.x + ((r.width - text.width) / 2), r.y + ((r.height + text.height) / 2));
        Imgproc.putText(im, label, pt, fontface, scale, new Scalar(255, 0, 0), thickness);
    }
}
