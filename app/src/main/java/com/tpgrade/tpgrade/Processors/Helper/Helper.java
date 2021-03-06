package com.tpgrade.tpgrade.Processors.Helper;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.text.DecimalFormat;

public class Helper {
    public static Point getPointCenter(Rect rect) {
        return new Point(rect.x + (rect.width / 2), rect.y + (rect.height / 2));
    }

    public static Mat getCMat(Mat frame, Rect rect) {
        Point center = getPointCenter(rect);
        return frame.submat(new Rect((int) center.x, (int) center.y, 1, 1));
    }

    public static double[] getColor(Mat frame, Rect rect) {
        return getCMat(frame, rect).get(0, 0);
    }

    public static double tinhDiem(int soCauDung, int tongSoCau, int heSoDiem) {
        if (tongSoCau == 0) {
            return 0.0;
        }
        return ((double) heSoDiem / tongSoCau) * soCauDung;
    }

    public static String formatNumber(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}
