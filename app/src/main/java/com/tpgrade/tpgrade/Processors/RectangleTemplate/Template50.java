package com.tpgrade.tpgrade.Processors.RectangleTemplate;

import com.tpgrade.Lib.ScreenUtils;
import com.tpgrade.tpgrade.Processors.Contants.RectangleContant;
import com.tpgrade.tpgrade.Processors.Interfaces.RectanglePointInterface;

import org.opencv.core.Point;

public class Template50 implements RectanglePointInterface {
    @Override
    public Point[] getR1Points() {
        int middleVertical = (int) ScreenUtils.getScreenWidth() / 2;

        Point p1, p2;
        p1 = new Point();
        p1.x = middleVertical - (2 * RectangleContant.RECTANGLE__WIDTH);
        p1.y = 0;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR2Points() {
        int middleVertical = (int) ScreenUtils.getScreenWidth() / 2;

        Point p1, p2;
        p1 = new Point();
        p1.x = middleVertical + (1 * RectangleContant.RECTANGLE__WIDTH);
        p1.y = 0;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR3Points() {
        int middleVertical = (int) ScreenUtils.getScreenWidth() / 2;

        Point p1, p2;
        p1 = new Point();
        p1.x = middleVertical - (2 * RectangleContant.RECTANGLE__WIDTH);
        p1.y = RectangleContant.RECTANGLE__HEIGHT * 4;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = p1.y + RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }

    @Override
    public Point[] getR4Points() {
        int middleVertical = (int) ScreenUtils.getScreenWidth() / 2;

        Point p1, p2;
        p1 = new Point();
        p1.x = middleVertical + (1 * RectangleContant.RECTANGLE__WIDTH);
        p1.y = RectangleContant.RECTANGLE__HEIGHT * 4;

        p2 = new Point();
        p2.x = p1.x + RectangleContant.RECTANGLE__WIDTH;
        p2.y = p1.y + RectangleContant.RECTANGLE__HEIGHT;

        Point[] points = new Point[]{p1, p2};
        return points;
    }
}
