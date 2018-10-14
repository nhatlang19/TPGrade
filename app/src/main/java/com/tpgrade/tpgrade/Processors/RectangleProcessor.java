package com.tpgrade.tpgrade.Processors;

import android.content.Context;

import com.tpgrade.tpgrade.Processors.Detect.Conners.DetectRectangle;
import com.tpgrade.tpgrade.Processors.Detect.Conners.ListDetectRectangle;
import com.tpgrade.tpgrade.Processors.Interfaces.RectanglePointInterface;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


/**
 * This class is a controller for puzzle game.
 * It converts the image from Camera into the shuffled image
 */
public class RectangleProcessor {
    private static final String TAG = "RectangleProcessor";

    RectanglePointInterface rectanglePoint;

    Context context;

    public RectangleProcessor(Context context, RectanglePointInterface rectanglePoint) {
        this.rectanglePoint = rectanglePoint;
        this.context = context;
    }

    /* This method is to make the processor know the size of the frames that
     * will be delivered via puzzleFrame.
     * If the frames will be different size - then the result is unpredictable
     */
    public synchronized void prepareData(int width, int height) {

    }

    /* this method to be called from the outside. it processes the frame and shuffles
     * the tiles as specified by mIndexes array
     */
    public synchronized Mat puzzleFrame(Mat inputPicture, Mat inputPictureGray) {
        drawGrid(inputPicture);

        ListDetectRectangle listDetectRectangle = new ListDetectRectangle();

        Point p1, p2;

        Point[] r1Points = this.rectanglePoint.getR1Points();
        p1 = r1Points[0];
        p2 = r1Points[1];
        Mat r0gt = inputPictureGray.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        Mat r0t = inputPicture.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        listDetectRectangle.add(new DetectRectangle(0, r0t, r0gt));

        Point[] r2Points = this.rectanglePoint.getR2Points();
        p1 = r2Points[0];
        p2 = r2Points[1];
        Mat r1gt = inputPictureGray.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        Mat r1t = inputPicture.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        listDetectRectangle.add(new DetectRectangle(1, r1t, r1gt));

        Point[] r3Points = this.rectanglePoint.getR3Points();
        p1 = r3Points[0];
        p2 = r3Points[1];
        Mat r2gt = inputPictureGray.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        Mat r2t = inputPicture.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        listDetectRectangle.add(new DetectRectangle(2, r2t, r2gt));

        Point[] r4Points = this.rectanglePoint.getR4Points();
        p1 = r4Points[0];
        p2 = r4Points[1];
        Mat r3gt = inputPictureGray.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        Mat r3t = inputPicture.submat((int) p1.y, (int) p2.y, (int) p1.x, (int) p2.x);
        listDetectRectangle.add(new DetectRectangle(3, r3t, r3gt));

        listDetectRectangle.detect();

        r0t.release();
        r1t.release();
        r2t.release();
        r3t.release();
        r0gt.release();
        r1gt.release();
        r2gt.release();
        r3gt.release();

        return inputPicture;
    }

    private void drawGrid(Mat drawMat) {
        Point[] r1Points = this.rectanglePoint.getR1Points();
        Point[] r2Points = this.rectanglePoint.getR2Points();
        Point[] r3Points = this.rectanglePoint.getR3Points();
        Point[] r4Points = this.rectanglePoint.getR4Points();

        Imgproc.rectangle(drawMat, r1Points[0], r1Points[1], new Scalar(0, 255, 0, 255), 2);
        Imgproc.rectangle(drawMat, r2Points[0], r2Points[1], new Scalar(0, 255, 0, 255), 2);
        Imgproc.rectangle(drawMat, r3Points[0], r3Points[1], new Scalar(0, 255, 0, 255), 2);
        Imgproc.rectangle(drawMat, r4Points[0], r4Points[1], new Scalar(0, 255, 0, 255), 2);
    }
}
