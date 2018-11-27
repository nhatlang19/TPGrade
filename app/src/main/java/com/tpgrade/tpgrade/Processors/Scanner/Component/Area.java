package com.tpgrade.tpgrade.Processors.Scanner.Component;


import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;
import com.tpgrade.tpgrade.Processors.Scanner.Helper.PerspectiveTransform;
import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class Area {
    static int VALID_NUMBER = 40;

    private MatOfPoint2f approxCurveMax;
    private int contourIdx;
    private int index;
    private int originalIndex;
    private List<MatOfPoint> contourList;
    private Mat gray;
    private Mat sourceMat;

    public MatOfPoint2f getApproxCurveMax() {
        return approxCurveMax;
    }

    public int getContourIdx() {
        return contourIdx;
    }

    public int getIndex() {
        return index;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public List<MatOfPoint> getContourList() {
        return contourList;
    }

    public Area(int index, int originalIndex, MatOfPoint2f approxCurveMax, int contourIdx, List<MatOfPoint> contourList, Mat gray, Mat sourceMat) {
        this.index = index;
        this.originalIndex = originalIndex;
        this.approxCurveMax = approxCurveMax;
        this.contourIdx = contourIdx;
        this.contourList = contourList;
        this.gray = gray;
        this.sourceMat = sourceMat;
    }

    public List<Integer> getAnswers() throws Exception {
        Mat copy = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
        Imgproc.drawContours(copy, contourList, originalIndex, new Scalar(0, 0, 255), 1);

        Mat warped = PerspectiveTransform.transform(gray, approxCurveMax);

        Mat paper = PerspectiveTransform.transform(sourceMat, approxCurveMax);

        Mat thresh = new Mat(warped.rows(), warped.cols(), CvType.CV_8UC3);
        Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        List<MatOfPoint> questionCnts = new ArrayList<>();
        List<MatOfPoint> cnts = new ArrayList<>();
        Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIdx = 0; contourIdx < cnts.size(); contourIdx++) {
            final MatOfPoint contour = cnts.get(contourIdx);
            final Rect bb = Imgproc.boundingRect(contour);
            float ar = bb.width / (float) bb.height;

            if (bb.width >= 16 && bb.height >= 16 && ar >= 0.6 && ar <= 1.2) {
                questionCnts.add(contour);
            }
        }

        if (questionCnts.size() != Area.VALID_NUMBER) {
            throw new Exception("Can not find circle");
        }

        int count = 0;
        for (int q = 0; q < questionCnts.size(); q++) {
            final MatOfPoint contour = questionCnts.get(q);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            Moments moment = Imgproc.moments(contour2f.col(0));
            int cX = (int) (moment.get_m10() / moment.get_m00());
            int cY = (int) (moment.get_m01() / moment.get_m00());

            final Rect bb = Imgproc.boundingRect(questionCnts.get(q));
        }

        // sort by y coordinates using the topleft point of every contour's bounding box
        Collections.sort(questionCnts, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = Double.compare(rect1.tl().y, rect2.tl().y);
                return result;
            }
        });

        // sort by x coordinates
        Collections.sort(questionCnts, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                Rect rect1 = Imgproc.boundingRect(o1);
                Rect rect2 = Imgproc.boundingRect(o2);
                int result = 0;
                double total = rect1.tl().y / rect2.tl().y;
                if (total <= 1.1) {
                    result = Double.compare(rect1.tl().x, rect2.tl().x);
                }
                return result;
            }
        });

        Mat copy5 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
        count = 0;
        for (int q = 0; q < questionCnts.size(); q++) {
            final MatOfPoint contour = questionCnts.get(q);
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            Moments moment = Imgproc.moments(contour2f.col(0));
            int cX = (int) (moment.get_m10() / moment.get_m00());
            int cY = (int) (moment.get_m01() / moment.get_m00());

            final Rect bb = Imgproc.boundingRect(questionCnts.get(q));
            Imgproc.drawContours(copy5, questionCnts, q, new Scalar(0, 0, 255), 1);
            Imgproc.putText(copy5, ++count + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
                    new Scalar(255, 255, 255), 2);
        }

        List<Question> questionsList = new ArrayList<>();
        for (int q = 0; q < questionCnts.size(); q += 4) {
            Question question = new Question();
            question.add(questionCnts.get(q));
            question.add(questionCnts.get(q + 1));
            question.add(questionCnts.get(q + 2));
            question.add(questionCnts.get(q + 3));
            questionsList.add(question);
        }

        List<Integer> questions = new ArrayList<>();

        for (Question question : questionsList) {
            int k = 0;
            int bubbleTotal = 0;
            for (int i = 0; i < question.cnts.size(); i++) {
                Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
                Imgproc.drawContours(mask, question.cnts, i, new Scalar(255, 255, 255), -1);
                // write("output/" + nameFile + "_10_" + i + ".jpg", mask);
                Core.bitwise_and(thresh, thresh, mask, mask);
                int total = Core.countNonZero(mask);
                if (total > bubbleTotal) {
                    bubbleTotal = total;
                    k = i;
                }
            }

            questions.add(k);
            Scalar scalar = new Scalar(0, 255, 0);
            Imgproc.drawContours(paper, question.cnts, k, scalar, 2);
        }

        return questions;
    }
}