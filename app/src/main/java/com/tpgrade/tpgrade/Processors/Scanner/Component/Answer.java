package com.tpgrade.tpgrade.Processors.Scanner.Component;


import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;
import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Answer {
    private List<Integer> questions;
    private List<Area> areaList;

    static List<Integer> AREAS = Arrays.asList(0, 2, 4, 6, 8);

    public Answer(ContourList cl, Gray _gray, Mat sourceMat) throws Exception {
        questions = new ArrayList<>();
        areaList = new ArrayList<>();
        Mat gray = _gray.getGray();
        List<MatOfPoint> contourList = cl.getContourList();

        int idx = 0;
        int contourIdxMax = 0;
        int count = 0;

        MatOfPoint2f approxCurveMax = new MatOfPoint2f();
        for (int contourIdx = 0; contourIdx < contourList.size(); contourIdx++) {
            final MatOfPoint contour = contourList.get(contourIdx);

            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            if (approxCurve.toArray().length == 4) {
                if (Helper.contains(Answer.AREAS, idx)) { // khung tra loi
                    approxCurveMax = approxCurve;
                    contourIdxMax = contourIdx;
                    Area area = new Area(count, idx, approxCurveMax, contourIdxMax, contourList, gray, sourceMat);
                    areaList.add(area);
                    count ++;
                }

                if (count >= 5) {
                    break;
                }
                idx++;
            }
        }

        this.handleEachArea();
    }

    private void handleEachArea() throws Exception
    {
        for(Area area : areaList) {
            getQuestions().addAll(area.getAnswers());
        }
    }

    public void printData() {
        for(int i = 0;i <getQuestions().size(); i++) {
            System.out.println(i + " : " + getQuestions().get(i));
        }
    }

    public List<Integer> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Integer> questions) {
        this.questions = questions;
    }
}
