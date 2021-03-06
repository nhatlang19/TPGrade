package com.tpgrade.tpgrade.Processors;

import android.content.Context;

import com.tpgrade.tpgrade.Processors.Detect.Conners.DetectRectangle;
import com.tpgrade.tpgrade.Processors.Interfaces.RectanglePointInterface;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;


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

        Mat original = new Mat();
        inputPicture.copyTo(original);
        drawGrid(inputPicture);

        Point[] r1Points = this.rectanglePoint.getR1Points();
        Point[] r2Points = this.rectanglePoint.getR2Points();
        Point[] r3Points = this.rectanglePoint.getR3Points();
        Point[] r4Points = this.rectanglePoint.getR4Points();
        List<Point[]> listPoints = Arrays.asList(r1Points, r2Points, r3Points, r4Points);
        DetectRectangle rectangle = new DetectRectangle(this.context, inputPicture, inputPictureGray, original, listPoints);
        rectangle.detect();

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
