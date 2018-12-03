package com.tpgrade.tpgrade.Processors.Detect.Conners;

import android.content.Context;

import com.tpgrade.tpgrade.GlobalState;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetectRectangleBK {
    protected static int NUMBERS_CODE_CIRCLE_MAX = 9;
    protected static int NUMBERS_CODE_MAX = 3;

    protected Mat dst;
    protected Mat gray;

    protected Mat original;

    protected Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG, cirIMG;
    protected MatOfPoint2f approxCurve;

    protected int threshold;

    protected List<Point[]> listPoints;

    protected Rect rectMaDe;

    protected String maDe = "";

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

        rectMaDe = null;

        //initialize treshold
        threshold = 100;

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

        Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<MatOfPoint> contoursRect = new ArrayList<>();
        List<MatOfPoint> contoursCircle = new ArrayList<>();
        List<MatOfPoint> khungAnswers = new ArrayList<>();
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
                    if (rect.width < rect.height) {
                        khungAnswers.add(cnt);
                        drawRectPink(dst, cnt);
                    } else {
                        contoursRect.add(cnt);
                    }
                }
            } else {
                Rect bb = Imgproc.boundingRect(cnt);
                float ar = bb.width / (float) bb.height;
                if (bb.width >= 25 && bb.height >= 25 && ar >= 0.9 && ar <= 1.2) {
                    drawRectBlue(dst, cnt);
                    contoursCircle.add(cnt);
                }
            }
        }


        Rect rMade = new Rect();
        int iMade = 0;
        int idx = 0;
        for (MatOfPoint cnt : contoursRect) {
            Rect rect = Imgproc.boundingRect(cnt);
            int index = areaAllow(rect, this.listPoints);
            if (index != -1 && index < 4) { // 4 corners
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

                drawRect(dst, cnt);
            } else if (rect.width > rect.height) {
                if (100 < rect.width && rect.width < 200) { // ma de
                    if (rMade.width == 0 || rMade.width > rect.width) {
                        rMade = rect.clone();
                        iMade = idx;
                    }
                } else if (rect.width > 200) { // ignore
                    //drawRectBlue(dst, cnt);
                }
            }
            idx++;
        }
        
        if (rMade.width != 0) { // handle made
            drawRectGreen(dst, contoursRect.get(iMade));
            handleCode(dst, original, rMade, contoursCircle);
        }

        Imgproc.putText(dst, contoursCircle.size() + "", new Point(100, 100), Core.FONT_HERSHEY_SIMPLEX, 1,
                new Scalar(0, 255, 255), 5);
        // handle khung tra loi
        Imgproc.putText(dst, khungAnswers.size() + "", new Point(50, 50), Core.FONT_HERSHEY_SIMPLEX, 1,
                new Scalar(255, 255, 255), 5);

        MatOfPoint[] khungs = new MatOfPoint[4];
        for (MatOfPoint cnt : khungAnswers) {

        }

        return dst;
    }

    /*
     * public synchronized Mat detect() {
     * Imgproc.pyrDown(gray, dsIMG, new Size(gray.cols() / 2, gray.rows() / 2));
     * Imgproc.pyrUp(dsIMG, usIMG, gray.size());
     * Imgproc.Canny(usIMG, bwIMG, 0, threshold);
     * Imgproc.dilate(bwIMG, bwIMG, new Mat(), new Point(-1, 1), 1);
     * List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
     * cIMG = bwIMG.clone();
     * cirIMG = bwIMG.clone();
     * Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
     * for (MatOfPoint cnt : contours) {
     * MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());
     * Imgproc.approxPolyDP(curve, approxCurve, 0.02 * Imgproc.arcLength(curve, true), true);
     * int numberVertices = (int) approxCurve.total();
     * double contourArea = Imgproc.contourArea(cnt);
     * if (Math.abs(contourArea) < 100) {
     * continue;
     * }
     * //Rectangle detected
     * if (numberVertices >= 4 && numberVertices <= 6) {
     * List<Double> cos = new ArrayList<>();
     * for (int j = 2; j < numberVertices + 1; j++) {
     * cos.add(angle(approxCurve.toArray()[j % numberVertices], approxCurve.toArray()[j - 2], approxCurve.toArray()[j - 1]));
     * }
     * Collections.sort(cos);
     * double mincos = cos.get(0);
     * double maxcos = cos.get(cos.size() - 1);
     * if (numberVertices == 4 && mincos >= -0.1 && maxcos <= 0.3) {
     * Rect rect = Imgproc.boundingRect(cnt);
     * int index = areaAllow(rect, this.listPoints);
     * if (index != -1 && index < 4) {
     * drawRect(dst, cnt);
     * } else if (rect.width > rect.height) {
     * if (100 < rect.width && rect.width < 200) {
     * drawRectGreen(dst, cnt);
     * rectMaDe = rect;
     * //handleCode(dst, rect, contours);
     * } else if (rect.width > 200) {
     * drawRectBlue(dst, cnt);
     * }
     * } else if (rect.width < rect.height) {
     * drawRectPink(dst, cnt);
     * }
                            Point p = null;
     * //                        switch(index) {
     * //                            case 0:
     * //                                p = new Point(rect.x, rect.y);
     * //                                break;
     * //                            case 1:
     * //                                p = new Point(rect.x + rect.width, rect.y);
     * //                                break;
     * //                            case 2:
     * //                                p = new Point(rect.x, rect.y + rect.height);
     * //                                break;
     * //                            case 3:
     * //                                p = new Point(rect.x + rect.width, rect.y + rect.height);
     * //                                break;
     * //                        }
     * //                        GlobalState.updateRect(index, 1, p);
     * //
     * //                        drawRect(dst, cnt);
     * //
     * //                        if (GlobalState.isValid()) {
     * //                            if (!CaptureImage.DID_CAPTURE) {
     * //                                List<Point> points = GlobalState.getRectPoint();
     * //                                Point p1 = Helper.min(points);
     * //                                Point p2 = Helper.max(points);
     * //
     * //                                Mat origin = this.original.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
     * //
     * ////                                ScannerMachine scanner = new ScannerMachine(origin);
     * ////                                try {
     * ////                                    scanner.handle();
     * ////
     * ////                                    System.out.println("CCCCODE: " + scanner.getCode().getCode());
     * ////                                    scanner.getAnswer().printData();
     * ////                                    String path = CaptureImage.doCapture(origin);
     * ////                                    Intent intent = new Intent(context, ContestKeyViewImageActivity.class);
     * ////                                    intent.putExtra(ContantContest.CONTEST_KEY_VIEW_IMAGE_PATH, path);
     * ////                                    context.startActivity(intent);
     * ////                                } catch (Exception e) {
     * ////                                    e.printStackTrace();
     * ////                                }
     * ////                                MatOfPoint2f contour2f = new MatOfPoint2f(GlobalState.getRectPointArray());
     * ////                                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
     * ////                                MatOfPoint2f approxCurve = new MatOfPoint2f();
     * ////                                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
     * //////
     * ////                                Mat paper = PerspectiveTransform.transform(origin, approxCurve);
     * ////
     * ////
     * //                                String path = CaptureImage.doCapture(origin);
     * //                                Intent intent = new Intent(context, ContestKeyViewImageActivity.class);
     * //                                intent.putExtra(ContantContest.CONTEST_KEY_VIEW_IMAGE_PATH, path);
     * //                                context.startActivity(intent);
     * //                            }
     * //                        }
     * //                    }
     * }
     * } else {
     * if (rectMaDe != null) {
     * drawRectBlue(dst, cnt);
     * }
     * }
     * }
     * <p>
     * return dst;
     * }
     */
    protected void handleCode(Mat im, Mat origin, Rect rect, List<MatOfPoint> contours) {

        List<MatOfPoint> numbers = new ArrayList<>();

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            MatOfPoint contour = contours.get(contourIdx);
            Rect bb = Imgproc.boundingRect(contour);

            if (rect.x <= bb.x && bb.x <= rect.x + rect.width && rect.y <= bb.y && bb.y <= rect.y + rect.height) {
                numbers.add(contour);
            }
        }
        if (numbers.size() == NUMBERS_CODE_CIRCLE_MAX) {
            // sort by y coordinates using the topleft point of every contour's bounding box
            Collections.sort(numbers, new Comparator<MatOfPoint>() {
                @Override
                public int compare(MatOfPoint o1, MatOfPoint o2) {
                    Rect rect1 = Imgproc.boundingRect(o1);
                    Rect rect2 = Imgproc.boundingRect(o2);
                    int result = Double.compare(rect1.tl().y, rect2.tl().y);
                    return result;
                }
            });

            // sort by x coordinates
            Collections.sort(numbers, new Comparator<MatOfPoint>() {
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
            for (int contourIdx = 0; contourIdx < numbers.size(); contourIdx++) {
                Mat thresh = new Mat(cIMG.rows(), cIMG.cols(), CvType.CV_8UC3);
                Imgproc.threshold(cIMG, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

                Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
                Imgproc.drawContours(mask, numbers, contourIdx, new Scalar(255, 255, 255), -1);
                Core.bitwise_and(thresh, thresh, mask, mask);

//                MatOfPoint contour = contours.get(contourIdx);
//                MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//                Moments moment = Imgproc.moments(contour2f.col(0));
//                int cX = (int) (moment.get_m10() / moment.get_m00());
//                int cY = (int) (moment.get_m01() / moment.get_m00());
//
//                Imgproc.putText(im, ++contourIdx + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
//                        new Scalar(255, 255, 255), 2);

                int total = Core.countNonZero(mask);
                if (total > 250) {
                    Imgproc.putText(im, total + "", new Point(total, total), Core.FONT_HERSHEY_SIMPLEX, 1,
                            new Scalar(255, 255, 255), 5);
                    maDe += contourIdx + "";
                    Imgproc.drawContours(im, numbers, contourIdx, new Scalar(0, 255, 0), 2);
                    Imgproc.drawContours(origin, numbers, contourIdx, new Scalar(0, 255, 0), 2);
                }
            }

//            if (maDe.length() == NUMBERS_CODE_MAX) {
//                Imgproc.putText(im, maDe, new Point(50, 50), Core.FONT_HERSHEY_SIMPLEX, 1,
//                        new Scalar(255, 255, 255), 5);
//            }
        }
    }

    protected void handleCode2(Mat im, Rect rect, List<MatOfPoint> cnts) {
//        Mat origin = this.original.submat(rect);
//
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(origin, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            final MatOfPoint contour = contours.get(contourIdx);
//            final Rect bb = Imgproc.boundingRect(contour);
//            float ar = bb.width / (float) bb.height;
//            System.out.println(contours.size());
//            System.out.println("width:" + bb.width + ",height: " + bb.height + ",ar: " + ar);
//
//            if ( rect.x <= bb.x && bb.x <= rect.x + rect.width && rect.y <= bb.y && bb.y <= rect.y + rect.height) {
//                Imgproc.drawContours(im, contours, contourIdx, new Scalar(0, 0, 255), 1);
//            }
        //if (bb.width >= 15 && bb.height >= 15 && ar >= 0.9 && ar <= 1.2) {

        //}

//        }


    }

    protected void drawRect(Mat im, MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);

        int thickness = 3;//1;
        Imgproc.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), thickness);
    }

    protected void drawRectPink(Mat im, MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);

        int thickness = 3;//1;
        Imgproc.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 255), thickness);
    }

    protected void drawRectBlue(Mat im, MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);

        int thickness = 3;//1;
        Imgproc.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), thickness);
    }

    protected void drawRectGreen(Mat im, MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);

        int thickness = 3;//1;
        Imgproc.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), thickness);
    }

}
