package com.tpgrade.tpgrade.Processors.Detect.Conners;

import android.graphics.Bitmap;
import android.util.Log;

import com.tpgrade.Lib.FileUtils;
import com.tpgrade.tpgrade.GlobalState;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
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

    protected int id;
    protected Mat dst;
    protected Mat gray;

    protected Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG;
    protected MatOfPoint2f approxCurve;

    protected int threshold;

    protected List<Point[]> listPoints;

    public DetectRectangle(int id, Mat dst, Mat gray, List<Point[]> listPoints) {
        this.id = id;
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

        this.listPoints = listPoints;
    }

    protected static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    protected static boolean areaValid(Point p1, Point p2, Point[] rPoints)
    {
        return rPoints[0].x <= p1.x && p1.x <= rPoints[1].x && rPoints[0].y <= p1.y && p1.y <= rPoints[1].y
                && rPoints[0].x <= p2.x && p2.x <= rPoints[1].x && rPoints[0].y <= p2.y && p2.y <= rPoints[1].y;
    }

    protected static int areaAllow(Rect rect, List<Point[]> listPoints)
    {
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
                    Rect rect = Imgproc.boundingRect(cnt);

                    int index = areaAllow(rect, this.listPoints);
                    if (index != -1 && index < 4) {
                        GlobalState.updateRect(index, 1, new Point(rect.x, rect.y));

                        drawRect(dst, cnt);

                        if (GlobalState.isValid()) {
                            drawRectFromMatOfPoint(dst, GlobalState.getRectPoint());
                        }
                    }
                }
            }
        }

        return dst;
    }

    protected void drawRectFromMatOfPoint(Mat im, List<Point> rectPoint)
    {
        Point point0 = rectPoint.get(0);
        Point point1 = rectPoint.get(1);
        Point point2 = rectPoint.get(2);
        Point point3 = rectPoint.get(3);

        int thickness = 1;//1;
        Imgproc.rectangle(im, new Point(point0.x, point0.y), new Point(point1.x, point1.y), new Scalar(255, 0, 0), thickness);
        Imgproc.rectangle(im, new Point(point1.x, point1.y), new Point(point2.x, point2.y), new Scalar(255, 0, 0), thickness);
        Imgproc.rectangle(im, new Point(point2.x, point2.y), new Point(point3.x, point3.y), new Scalar(255, 0, 0), thickness);
        Imgproc.rectangle(im, new Point(point3.x, point3.y), new Point(point0.x, point0.y), new Scalar(255, 0, 0), thickness);
    }

    protected void drawRect(Mat im, MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);

        int thickness = 3;//1;
        Imgproc.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), thickness);
    }

    protected void captureImage()
    {
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bmp);
            FileUtils.storePhotoOnDisk(bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
    }

}
