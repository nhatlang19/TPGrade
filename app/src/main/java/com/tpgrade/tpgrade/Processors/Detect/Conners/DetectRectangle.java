package com.tpgrade.tpgrade.Processors.Detect.Conners;

import android.content.Context;
import android.content.Intent;

import com.tpgrade.contants.ContantContest;
import com.tpgrade.models.Exam;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.ContestKeyViewImageActivity;
import com.tpgrade.tpgrade.GlobalState;
import com.tpgrade.tpgrade.Processors.CaptureImage;
import com.tpgrade.tpgrade.Processors.Helper.Drawing;
import com.tpgrade.tpgrade.Processors.Helper.Helper;
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

public class DetectRectangle {
    protected Mat dst;
    protected Mat gray;
    protected Mat original;
    protected Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG, cirIMG;
    protected MatOfPoint2f approxCurve;
    protected int threshold;
    protected List<Point[]> listPoints;
    protected List<Question> questions;
    Context context;

    protected String maDe;
    protected double diem;

    protected int thickness = 2;

    public DetectRectangle(Context context, Mat dst, Mat gray, Mat original, List<Point[]> listPoints) {
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
        threshold = 200;

        questions = new ArrayList<>();

        this.listPoints = listPoints;

        maDe = "";
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
        dsIMG = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
        Imgproc.GaussianBlur(gray, dsIMG, new Size(5, 5), 0);

        bwIMG = new Mat(dsIMG.rows(), dsIMG.cols(), CvType.CV_8UC3);
        Imgproc.Canny(dsIMG, bwIMG, 75, 300);

        List<MatOfPoint> contours = new ArrayList<>();

        cIMG = bwIMG.clone();
        cirIMG = bwIMG.clone();

        Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

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
                        Drawing.drawRect(original, cnt);
                    } else if (rect.width > rect.height) {
                        if (rect.width >= 25 && rect.height <= 30 && rect.height >= 20) {
                            contoursGreen.add(cnt);
                        }
                    }
                }
            }
        }

        if (contoursGreen.size() == 16) {
            contoursGreen = Sort.doSort(contoursGreen);
            handleGreen(contoursGreen);

            handleCode(contoursGreen);

            if (GlobalState.isValid() && maDe.length() == 4) {
                GlobalState global = (GlobalState) context.getApplicationContext();
                long currentTopicId = global.getSelectedTopicId();
                Topic topic = Topic.findById(Topic.class, currentTopicId);
                List<Exam> exams = Exam.find(Exam.class, "exam_title = ? and topic = ?", new String[]{maDe, String.valueOf(currentTopicId)}, null, null, "1");

                List<String> examAnswer = new ArrayList<>();
                if (!exams.isEmpty()) {
                    Exam exam = exams.get(0);
                    examAnswer = exam.getAnswers();
                }
                List<Integer> answers = handleAnswer(examAnswer, contoursGreen);
                if (answers.size() >= topic.numbers) {
                    int correct = 0;
                    int count = 0;
                    if (!exams.isEmpty()) {
                        count = examAnswer.size();
                        for (int i = 0; i < count; i++) {
                            if (answers.get(i) >= 0 && answers.get(i) == parseAnswer(examAnswer.get(i))) {
                                correct++;
                            }
                        }
                    }

                    this.diem = Helper.tinhDiem(correct, count, topic.topScore);

                    List<Point> pointList = GlobalState.getRectPoint();
                    Point p0 = pointList.get(0);
                    Point p1 = pointList.get(1);
                    Point p2 = pointList.get(2);
                    Point p3 = pointList.get(3);

                    Imgproc.putText(dst, Helper.formatNumber(this.diem) + "", new Point(p1.x - 200, p1.y + 150), Core.FONT_HERSHEY_SIMPLEX, 3,
                            new Scalar(255, 0, 0, 255), 2);
                    Imgproc.putText(original, Helper.formatNumber(this.diem) + "", new Point(p1.x - 200, p1.y + 150), Core.FONT_HERSHEY_SIMPLEX, 3,
                            new Scalar(255, 0, 0, 255), 2);

                    this.saveImage(p0, p3);
                }
            }
        }

        return dst;
    }

    protected void saveImage(Point p0, Point p3) {
        Mat origin = this.original.submat(new Rect((int) p0.x, (int) p0.y, (int) (p3.x - p0.x), (int) (p3.y - p0.y)));
        String path = CaptureImage.doCapture(origin);
        Intent intent = new Intent(context, ContestKeyViewImageActivity.class);
        intent.putExtra(ContantContest.CONTEST_KEY_VIEW_IMAGE_PATH, path);
        context.startActivity(intent);

    }

    protected int parseAnswer(String key) {
        int result = -1;
        switch (key) {
            case "A":
                result = 0;
                break;
            case "B":
                result = 1;
                break;
            case "C":
                result = 2;
                break;
            case "D":
                result = 3;
                break;
        }

        return result;
    }

    protected void handleCode(List<MatOfPoint> contours) {
        int[] positions = new int[]{0, 1, 2, 3};
        this.doHandleCode(contours, positions);
    }

    protected List<Integer> handleAnswer(List<String> examAnswer, List<MatOfPoint> contours) {
        List<Integer> answers = new ArrayList<>();
        answers.addAll(this.handleZone1(examAnswer, contours));
        answers.addAll(this.handleZone2(examAnswer, contours));
        answers.addAll(this.handleZone3(examAnswer, contours));
        answers.addAll(this.handleZone4(examAnswer, contours));
        answers.addAll(this.handleZone5(examAnswer, contours));

        return answers;
    }

    protected List<Integer> handleZone1(List<String> examAnswer, List<MatOfPoint> contours) {
        int[] positions = new int[]{4, 5, 10, 11};
        return this.doHandle(contours, positions, examAnswer, 0);
    }

    protected List<Integer> handleZone2(List<String> examAnswer, List<MatOfPoint> contours) {
        int[] positions = new int[]{5, 6, 11, 12};
        return this.doHandle(contours, positions, examAnswer, 10);
    }

    protected List<Integer> handleZone3(List<String> examAnswer, List<MatOfPoint> contours) {
        int[] positions = new int[]{6, 7, 12, 13};
        return this.doHandle(contours, positions, examAnswer, 20);
    }

    protected List<Integer> handleZone4(List<String> examAnswer, List<MatOfPoint> contours) {
        int[] positions = new int[]{7, 8, 13, 14};
        return this.doHandle(contours, positions, examAnswer, 30);
    }

    protected List<Integer> handleZone5(List<String> examAnswer, List<MatOfPoint> contours) {
        int[] positions = new int[]{8, 9, 14, 15};
        return this.doHandle(contours, positions, examAnswer, 40);
    }

    protected List<Integer> doHandle(List<MatOfPoint> contours, int[] positions, List<String> answersData, int pstAnswer) {
        List<Integer> answers = new ArrayList<>();
        try {
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

            int cX = rect1.x + rect1.width;
            int cY = rect1.y + rect1.height;

            Point p1 = new Point(rect1.x + rect1.width, rect1.y + rect1.height);
            Point p2 = new Point(rect2.x, rect2.y + rect2.height);
            Point p3 = new Point(rect3.x + rect3.width, rect3.y);
            Point p4 = new Point(rect4.x, rect4.y);

            double tbW = (p2.x - p1.x) / 4;
            double tbH = (p3.y - p1.y) / 10;

            double x;
            double y = p1.y;
            Mat frameROI = new Mat(cIMG, new Rect(cX, cY, rect4.x - cX, rect4.y - cY));

            double tX = 0;
            double tY = 0;
            int allowLimit = 20;

            int k;
            int dapAn;
            int bubbleTotal;
            Point keepStart, keepEnd;
            Point keepStartRight, keepEndRight;
            for (int i = 0; i < 10; i++) {
                dapAn = -2;
                if (answersData.size() > i + pstAnswer) {
                    dapAn = this.parseAnswer(answersData.get(i + pstAnswer));
                }
                x = p1.x;
                tX = 0;
                keepStart = keepEnd = null;
                keepStartRight = keepEndRight = null;

                bubbleTotal = -1;
                k = -1;
                for (int j = 0; j < 4; j++) {
                    Point pStart = new Point(x, y);
                    Point pEnd = new Point(x + tbW, y + tbH);

                    Point tStart = new Point(tX, tY);
                    Point tEnd = new Point(tX + tbW, tY + tbH);

                    Mat mask = Mat.zeros(frameROI.rows(), frameROI.cols(), CvType.CV_8U);
                    Imgproc.rectangle(mask, tStart, tEnd, new Scalar(255, 255, 255), -1);

                    Core.bitwise_and(frameROI, frameROI, mask, mask);
                    int total = Core.countNonZero(mask);

                    if (total > allowLimit && total > bubbleTotal) {
                        bubbleTotal = total;
                        keepStart = pStart;
                        keepEnd = pEnd;
                        k = j;
                    }
                    Imgproc.rectangle(dst, pStart, pEnd, new Scalar(0, 0, 255, 255), thickness);

                    if (j == dapAn) {
                        keepStartRight = pStart;
                        keepEndRight = pEnd;
                    }

                    mask.release();
                    x += tbW;
                    tX += tbW;
                }

                if (bubbleTotal != -1) {
                    if (k == dapAn) {
                        if (keepStart != null && keepEnd != null) {
                            Imgproc.rectangle(dst, keepStart, keepEnd, new Scalar(0, 255, 0, 255), thickness);
                            Imgproc.rectangle(original, keepStart, keepEnd, new Scalar(0, 255, 0, 255), thickness);
                        }
                    } else {
                        if (keepStartRight != null && keepEndRight != null) {
                            Imgproc.rectangle(dst, keepStartRight, keepEndRight, new Scalar(255, 0, 0, 255), thickness);
                            Imgproc.rectangle(original, keepStartRight, keepEndRight, new Scalar(255, 0, 0, 255), thickness);
                        }
                    }
                } else {
                    if (keepStartRight != null && keepEndRight != null) {
                        Imgproc.rectangle(dst, keepStartRight, keepEndRight, new Scalar(255, 0, 0, 255), thickness);
                        Imgproc.rectangle(original, keepStartRight, keepEndRight, new Scalar(255, 0, 0, 255), thickness);
                    }
                }
                answers.add(k);

                y += tbH;
                tY += tbH;
            }
            frameROI.release();
            return answers;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return answers;
    }

    protected void doHandleCode(List<MatOfPoint> contours, int[] positions) {
        try {
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

            int cX = rect1.x + rect1.width + 1;
            int cY = rect1.y + rect1.height + 1;

            Point p1 = new Point(rect1.x + rect1.width, rect1.y + rect1.height);
            Point p2 = new Point(rect2.x, rect2.y + rect2.height);
            Point p3 = new Point(rect3.x + rect3.width, rect3.y);
            Point p4 = new Point(rect4.x, rect4.y);

            double tbW = (p2.x - p1.x) / 4;
            double tbH = (p3.y - p1.y) / 4;

            double x;
            double y = p1.y;
            Mat frameROI = new Mat(cIMG, new Rect(cX, cY, rect4.x - cX, rect4.y - cY));
            int bubbleTotal;
            double tX = 0;
            double tY = 0;
            Point keepStart = null;
            Point keepEnd = null;
            int k = 0;
            for (int i = 0; i < 4; i++) {
                x = p1.x;
                tX = 0;
                keepStart = null;
                keepEnd = null;
                bubbleTotal = -1;
                k = 0;
                for (int j = 0; j < 4; j++) {
                    Point pStart = new Point(x, y);
                    Point pEnd = new Point(x + tbW, y + tbH);

                    Point tStart = new Point(tX, tY);
                    Point tEnd = new Point(tX + tbW, tY + tbH);

                    Mat mask = Mat.zeros(frameROI.rows(), frameROI.cols(), CvType.CV_8U);
                    Imgproc.rectangle(mask, tStart, tEnd, new Scalar(255, 255, 255), -1);
                    Core.bitwise_and(frameROI, frameROI, mask, mask);
                    int total = Core.countNonZero(mask);
                    if (total > bubbleTotal) {
                        bubbleTotal = total;
                        keepStart = pStart;
                        keepEnd = pEnd;
                        k = j;
                    }
                    Imgproc.rectangle(dst, pStart, pEnd, new Scalar(0, 0, 255, 255), thickness);

                    mask.release();

                    x += tbW;
                    tX += tbW;
                }

                if (bubbleTotal != -1) {
                    Imgproc.rectangle(dst, keepStart, keepEnd, new Scalar(0, 255, 0, 255), thickness);
                    Imgproc.rectangle(original, keepStart, keepEnd, new Scalar(0, 255, 0, 255), thickness);
                    this.maDe += k + "";
                }

                y += tbH;
                tY += tbH;
            }

            Imgproc.putText(dst, "MADE:" + this.maDe, new Point(250, 80), Core.FONT_HERSHEY_SIMPLEX, 2,
                    new Scalar(255, 255, 255, 255), 2);
            Imgproc.putText(original, "MADE:" + this.maDe, new Point(250, 80), Core.FONT_HERSHEY_SIMPLEX, 2,
                    new Scalar(255, 255, 255, 255), 2);
            frameROI.release();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected void handleGreen(List<MatOfPoint> contours) {

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            MatOfPoint contour = contours.get(contourIdx);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            Moments moment = Imgproc.moments(contour2f.col(0));
            int cX = (int) (moment.get_m10() / moment.get_m00());
            int cY = (int) (moment.get_m01() / moment.get_m00());

            // Rect rect = Imgproc.boundingRect(contour);
            Imgproc.putText(dst, contourIdx + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
                    new Scalar(255, 255, 255, 255), 2);
        }
    }
}
