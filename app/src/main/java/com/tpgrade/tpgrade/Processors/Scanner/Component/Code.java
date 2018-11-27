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

public class Code {
	private String _code = "";

	public String getCode() {
		return this._code;
	}

	public Code(ContourList cl, Gray _gray, Mat sourceMat) {
		Mat gray = _gray.getGray();
		List<MatOfPoint> contourList = cl.getContourList();

		int index = 0;
		int contourIdxMax = 0;
		MatOfPoint2f approxCurveMax = new MatOfPoint2f();
		for (int contourIdx = 0; contourIdx < contourList.size(); contourIdx++) {
			final MatOfPoint contour = contourList.get(contourIdx);

			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
			if (approxCurve.toArray().length == 4) {
				if (index == 22) { // khung ma de
					approxCurveMax = approxCurve;
					contourIdxMax = contourIdx;
					break;
				}
				index++;
			}
		}

		this.getCode(gray, sourceMat, approxCurveMax);
	}

	public void getCode(Mat gray, Mat sourceMat, MatOfPoint2f approxCurveMax) {
		Mat warped = PerspectiveTransform.transform(gray, approxCurveMax);

		Mat paper = PerspectiveTransform.transform(sourceMat, approxCurveMax);

		Mat thresh = new Mat(warped.rows(), warped.cols(), CvType.CV_8UC3);
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

		List<MatOfPoint> numbers = new ArrayList<>();
		List<MatOfPoint> cnts = new ArrayList<>();
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		for (int contourIdx = 0; contourIdx < cnts.size(); contourIdx++) {
			final MatOfPoint contour = cnts.get(contourIdx);
			final Rect bb = Imgproc.boundingRect(contour);
			float ar = bb.width / (float) bb.height;

			if (bb.width >= 15 && bb.height >= 15 && ar >= 0.9 && ar <= 1.2) {
				numbers.add(contour);
			}
		}

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

		for (int i = 0; i < numbers.size(); i++) {
			Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
			Imgproc.drawContours(mask, numbers, i, new Scalar(255, 255, 255), -1);
			Core.bitwise_and(thresh, thresh, mask, mask);

			int total = Core.countNonZero(mask);
			if (total > 200) { // TODO: need to improve this line
				this._code += i + "";
				Scalar scalar = new Scalar(0, 255, 0);
				Imgproc.drawContours(paper, numbers, i, scalar, 2);
			}
		}
	}
}