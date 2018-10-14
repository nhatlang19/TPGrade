package com.tpgrade.tpgrade.Processors.RectangleTemplate;

import com.tpgrade.Lib.ScreenUtils;
import com.tpgrade.tpgrade.Processors.Contants.RectangleContant;
import com.tpgrade.tpgrade.Processors.Interfaces.RectanglePointInterface;

import org.opencv.core.Point;

public class Template50 implements RectanglePointInterface {
    @Override
    public Point[] getR1Points() {
        Point p1, p2;
        p1 = new Point();
        p1.x = 0;
        p1.y = 0;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR2Points() {

        Point p1, p2;
        p1 = new Point();
        p1.x = ScreenUtils.getScreenWidth() - 3 * RectangleContant.RECTANGLE__WIDTH;
        p1.y = 0;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR3Points() {
        Point p1, p2;
        p1 = new Point();
        p1.x = 0;
        p1.y = ScreenUtils.getScreenHeight() - (1 * RectangleContant.RECTANGLE__HEIGHT);

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = p1.y + RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR4Points() {

        Point p1, p2;
        p1 = new Point();
        p1.x = ScreenUtils.getScreenWidth() - 3 * RectangleContant.RECTANGLE__WIDTH;
        p1.y = ScreenUtils.getScreenHeight() - (1 * RectangleContant.RECTANGLE__HEIGHT);

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = p1.y + RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }
}
