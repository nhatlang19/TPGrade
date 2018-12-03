package com.tpgrade.tpgrade.Processors.Detect.Conners2;

import android.content.Context;

import com.tpgrade.tpgrade.GlobalState;
import com.tpgrade.tpgrade.Processors.Helper.Drawing;
import com.tpgrade.tpgrade.Processors.Helper.Sort;
import com.tpgrade.tpgrade.Processors.Question;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetectRectangleBK {
    protected Mat dst;
    protected Mat gray;
    protected Mat original;
    protected Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG, cirIMG;
    protected MatOfPoint2f approxCurve;
    protected int threshold;
    protected List<Point[]> listPoints;
    protected List<Question> questions;
    Context context;

    public DetectRectangleBK(Context context, Mat dst, Mat gray, Mat original, List<Point[]> listPoints) {
        this.context = context;

        this.dst = dst;
        this.gray = gray;
        this.original = original;

        bwIMG = new Mat();
        dsIMG = new Mat();
        hsvIMG = new Mat();
        lrrIMG = new Mat();
        urrIMG = new Mat();
        usIMG = new Mat();
        cIMG = new Mat();
        cirIMG = new Mat();
        hovIMG = new Mat();
        approxCurve = new MatOfPoint2f();

        //initialize treshold
        threshold = 100;

        questions = new ArrayList<>();

        this.listPoints = listPoints;
    }

    protected static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    protected static boolean areaValid(Point p1, Point p2, Point[] rPoints) {
        return rPoints[0].x <= p1.x && p1.x <= rPoints[1].x && rPoints[0].y <= p1.y && p1.y <= rPoints[1].y
                && rPoints[0].x <= p2.x && p2.x <= rPoints[1].x && rPoints[0].y <= p2.y && p2.y <= rPoints[1].y;
    }

    protected static int areaAllow(Rect rect, List<Point[]> listPoints) {
        Point p1 = new Point(rect.x, rect.y);
        Point p2 = new Point(rect.x + rect.width, rect.y + rect.height);

        int index = 0;
        for (Point[] rPoints : listPoints) {
            if (areaValid(p1, p2, rPoints)) {
                return index;
            }
            index += 1;
        }

        return -1;
    }

    public synchronized Mat detect() {
        Imgproc.pyrDown(gray, dsIMG, new Size(gray.cols() / 2, gray.rows() / 2));
        Imgproc.pyrUp(dsIMG, usIMG, gray.size());

        Imgproc.Canny(usIMG, bwIMG, 0, threshold);

        Imgproc.dilate(bwIMG, bwIMG, new Mat(), new Point(-1, 1), 1);

        List<MatOfPoint> contours = new ArrayList<>();

        cIMG = bwIMG.clone();
        cirIMG = bwIMG.clone();

        Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> contoursRect = new ArrayList<>();
        List<MatOfPoint> contoursCircle = new ArrayList<>();
        List<MatOfPoint> contoursGreen = new ArrayList<>();
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
                    Rect rect = Imgproc.boundingRect(cnt);
                    int index = areaAllow(rect, this.listPoints);
                    if (index != -1 && index < 4 && rect.width < 30) { // 4 corners
                        Point p = null;
                        switch (index) {
                            case 0:
                                p = new Point(rect.x, rect.y);
                                break;
                            case 1:
                                p = new Point(rect.x + rect.width, rect.y);
                                break;
                            case 2:
                                p = new Point(rect.x, rect.y + rect.height);
                                break;
                            case 3:
                                p = new Point(rect.x + rect.width, rect.y + rect.height);
                                break;
                        }
                        GlobalState.updateRect(index, 1, p);

                        Drawing.drawRect(dst, cnt);
                    } else if (rect.width > rect.height) {
                        if (rect.width > 30 && rect.height <= 30 && rect.height >= 20) {
                            contoursGreen.add(cnt);
                            // Drawing.drawRectGreen(dst, cnt);
                        }
                    }
                }
            }
        }

        if (contoursGreen.size() == 16) {
            contoursGreen = Sort.doSort(contoursGreen);
            handleGreen(dst, original, contoursGreen);
            handleAnswer(dst, original, contoursGreen);
        }

        return dst;
    }

    protected void handleAnswer(Mat im, Mat origin, List<MatOfPoint> contours) {
        int correct = 0;

        this.handleZone1(im, origin, contours);
        this.handleZone2(im, origin, contours);
        this.handleZone3(im, origin, contours);
        this.handleZone4(im, origin, contours);
        this.handleZone5(im, origin, contours);
    }

    protected void handleZone1(Mat im, Mat origin, List<MatOfPoint> contours) {
        int[] positions = new int[]{4, 5, 10, 11};
        this.doHandle(im, origin, contours, positions);
    }

    protected void handleZone2(Mat im, Mat origin, List<MatOfPoint> contours) {
        int[] positions = new int[]{5, 6, 11, 12};
        this.doHandle(im, origin, contours, positions);
    }

    protected void handleZone3(Mat im, Mat origin, List<MatOfPoint> contours) {
        int[] positions = new int[]{6, 7, 12, 13};
        this.doHandle(im, origin, contours, positions);
    }

    protected void handleZone4(Mat im, Mat origin, List<MatOfPoint> contours) {
        int[] positions = new int[]{7, 8, 13, 14};
        this.doHandle(im, origin, contours, positions);
    }

    protected void handleZone5(Mat im, Mat origin, List<MatOfPoint> contours) {
        int[] positions = new int[]{8, 9, 14, 15};
        this.doHandle(im, origin, contours, positions);
    }

    protected void doHandle(Mat im, Mat origin, List<MatOfPoint> contours, int[] positions) {
        MatOfPoint mop_1 = contours.get(positions[0]);
        MatOfPoint mop_2 = contours.get(positions[1]);
        MatOfPoint mop_3 = contours.get(positions[2]);
        MatOfPoint mop_4 = contours.get(positions[3]);

        Rect rect1 = Imgproc.boundingRect(mop_1);
        Drawing.drawRectPink(dst, mop_1);
        Rect rect2 = Imgproc.boundingRect(mop_2);
        Drawing.drawRectPink(dst, mop_2);
        Rect rect3 = Imgproc.boundingRect(mop_3);
        Drawing.drawRectPink(dst, mop_3);
        Rect rect4 = Imgproc.boundingRect(mop_4);
        Drawing.drawRectPink(dst, mop_4);

        int cX = rect1.x + rect1.width + 5;
        int cY = rect1.y + rect1.height + 5;
        Mat frameROI = new Mat(cIMG, new Rect(cX, cY, rect4.x - cX, rect4.y - cY));

        List<MatOfPoint> cs = new ArrayList<>();
        List<MatOfPoint> circles = new ArrayList<>();
        Imgproc.findContours(frameROI, cs, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIdx = 0; contourIdx < cs.size(); contourIdx++) {
            MatOfPoint contour = cs.get(contourIdx);
            Rect rect = Imgproc.boundingRect(contour);

            float ar = rect.width / (float) rect.height;
            if (rect.width >= 20 && rect.height >= 20) {
                circles.add(contour);
            }
        }

        if (circles.size() != 40) {
            return;
        }

        circles = Sort.doSort(circles);

        this.debugCircle(im, circles, cX, cY);

        List<Question> questions = this.getQuestions(circles);

        Mat thresh = new Mat(frameROI.rows(), frameROI.cols(), CvType.CV_8UC3);
        Imgproc.threshold(frameROI, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        int thickness = 2;
        int idx = 0;
        int correct = 0;
        for (Question question : questions) {
            int k = 0;
            int bubbleTotal = 0;
            Rect rectMax = new Rect();
            for (int i = 0; i < question.cnts.size(); i++) {
                MatOfPoint contour = question.cnts.get(i);
                Rect rect = Imgproc.boundingRect(contour);
                Imgproc.rectangle(im, new Point(cX + rect.x, cY + rect.y), new Point(cX + rect.x + rect.width, cY + rect.y + rect.height), new Scalar(0, 0, 255), thickness);

                Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
                Imgproc.drawContours(mask, question.cnts, i, new Scalar(255, 255, 255), -1);
                Core.bitwise_and(thresh, thresh, mask, mask);
                int total = Core.countNonZero(mask);
                if (total > bubbleTotal) {
                    bubbleTotal = total;
                    rectMax = rect;
                    k = i; // vi tri tra loi dung
                }

                mask.release();
            }


            // Hien thi khung tra loi
            Point pStart = new Point(cX + rectMax.x, cY + rectMax.y);
            Point pEnd = new Point(cX + rectMax.x + rectMax.width, cY + rectMax.y + rectMax.height);
            Imgproc.rectangle(im, pStart, pEnd, new Scalar(0, 255, 0), thickness);

            // TODO: can check cau tra loi dung, dua theo ma de
        }

        thresh.release();
        frameROI.release();
    }

    protected List<MatOfPoint> getCircleContour(List<MatOfPoint> contours) {
        List<MatOfPoint> circles = new ArrayList<>();
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            MatOfPoint contour = contours.get(contourIdx);
            Rect rect = Imgproc.boundingRect(contour);

            float ar = rect.width / (float) rect.height;
            if (rect.width >= 20 && rect.height >= 20) {
                circles.add(contour);
            }
        }

        return circles;
    }

    protected List<Question> getQuestions(List<MatOfPoint> circles) {
        List<Question> questions = new ArrayList<>();
        for (int contourIdx = 0; contourIdx < circles.size(); contourIdx += 4) {
            Question question = new Question();
            question.add(circles.get(contourIdx));
            question.add(circles.get(contourIdx + 1));
            question.add(circles.get(contourIdx + 2));
            question.add(circles.get(contourIdx + 3));
            questions.add(question);
        }

        return questions;
    }

    protected void debugCircle(Mat im, List<MatOfPoint> circles, int cX, int cY) {
        Imgproc.putText(im, circles.size() + "", new Point(50, 50), Core.FONT_HERSHEY_SIMPLEX, 2,
                new Scalar(255, 255, 255), 2);
        for (int contourIdx = 0; contourIdx < circles.size(); contourIdx++) {
            MatOfPoint contour = circles.get(contourIdx);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            Moments moment = Imgproc.moments(contour2f.col(0));
            int cXX = (int) (moment.get_m10() / moment.get_m00());
            int cYY = (int) (moment.get_m01() / moment.get_m00());

            Imgproc.putText(im, contourIdx + "", new Point(cXX - 20 + cX, cYY + cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
                    new Scalar(255, 255, 255), 2);
        }
    }

    protected void handleGreen(Mat im, Mat origin, List<MatOfPoint> contours) {

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            MatOfPoint contour = contours.get(contourIdx);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            Moments moment = Imgproc.moments(contour2f.col(0));
            int cX = (int) (moment.get_m10() / moment.get_m00());
            int cY = (int) (moment.get_m01() / moment.get_m00());

            Imgproc.putText(im, contourIdx + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 2,
                    new Scalar(255, 255, 255), 2);
        }

        Imgproc.putText(dst, contours.size() + "", new Point(200, 200), Core.FONT_HERSHEY_SIMPLEX, 2,
                new Scalar(0, 255, 255), 2);
    }
}
