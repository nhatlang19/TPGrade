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

		if (ScannerMachine.DEBUG == true) {
			Mat copy = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
			Imgproc.drawContours(copy, contourList, contourIdxMax, new Scalar(0, 0, 255), 1);
			Helper.write("output/" + ScannerMachine.nameFile + "_3_code.jpg", copy);
		}

		this.getCode(gray, sourceMat, approxCurveMax);
	}

	public void getCode(Mat gray, Mat sourceMat, MatOfPoint2f approxCurveMax) {
		Mat warped = PerspectiveTransform.transform(gray, approxCurveMax);
		Helper.write("output/" + ScannerMachine.nameFile + "_3_code_transform.jpg", warped);

		Mat paper = PerspectiveTransform.transform(sourceMat, approxCurveMax);
		Helper.write("output/" + ScannerMachine.nameFile + "_3_code_transform_paper.jpg", paper);

		Mat thresh = new Mat(warped.rows(), warped.cols(), CvType.CV_8UC3);
		Imgproc.threshold(warped, thresh, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
		Helper.write("output/" + ScannerMachine.nameFile + "_3_code_binarize.jpg", thresh);

		List<MatOfPoint> numbers = new ArrayList<>();
		List<MatOfPoint> cnts = new ArrayList<>();
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat copy3 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
		for (int contourIdx = 0; contourIdx < cnts.size(); contourIdx++) {
			final MatOfPoint contour = cnts.get(contourIdx);
			final Rect bb = Imgproc.boundingRect(contour);
			float ar = bb.width / (float) bb.height;

			System.out.println("width:" + bb.width + ",height: " + bb.height + ",ar: " + ar);
			if (bb.width >= 15 && bb.height >= 15 && ar >= 0.9 && ar <= 1.2) {
				numbers.add(contour);
				if (ScannerMachine.DEBUG == true) {
					Imgproc.drawContours(copy3, cnts, contourIdx, new Scalar(0, 0, 255), 1);
				}
			}
		}
		Helper.write("output/" + ScannerMachine.nameFile + "_3_code_find_circle.jpg", copy3);
		System.out.println(numbers.size());

		if (ScannerMachine.DEBUG == true) {
			Mat copy4 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
			int count = 0;
			for (int q = 0; q < numbers.size(); q++) {
				final MatOfPoint contour = numbers.get(q);
				MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
				Moments moment = Imgproc.moments(contour2f.col(0));
				int cX = (int) (moment.get_m10() / moment.get_m00());
				int cY = (int) (moment.get_m01() / moment.get_m00());

				final Rect bb = Imgproc.boundingRect(numbers.get(q));
				Imgproc.drawContours(copy4, numbers, q, new Scalar(0, 0, 255), 1);
				Imgproc.putText(copy4, ++count + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
						new Scalar(255, 255, 255), 2);
			}
			Helper.write("output/" + ScannerMachine.nameFile + "_3_code_number.jpg", copy4);
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

		if (ScannerMachine.DEBUG == true) {
			Mat copy5 = new Mat(thresh.rows(), thresh.cols(), CvType.CV_8UC3);
			int count = 0;
			for (int q = 0; q < numbers.size(); q++) {
				final MatOfPoint contour = numbers.get(q);
				MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
				Moments moment = Imgproc.moments(contour2f.col(0));
				int cX = (int) (moment.get_m10() / moment.get_m00());
				int cY = (int) (moment.get_m01() / moment.get_m00());

				final Rect bb = Imgproc.boundingRect(numbers.get(q));
				Imgproc.drawContours(copy5, numbers, q, new Scalar(0, 0, 255), 1);
				Imgproc.putText(copy5, ++count + "", new Point(cX - 20, cY), Core.FONT_HERSHEY_SIMPLEX, 0.5,
						new Scalar(255, 255, 255), 2);
			}
			Helper.write("output/" + ScannerMachine.nameFile + "_3_code_number_after_sort.jpg", copy5);
		}

		for (int i = 0; i < numbers.size(); i++) {
			Mat mask = Mat.zeros(thresh.rows(), thresh.cols(), CvType.CV_8U);
			Imgproc.drawContours(mask, numbers, i, new Scalar(255, 255, 255), -1);
			Helper.write("output/" + ScannerMachine.nameFile + "_3_code_" + i + ".jpg", mask);
			Core.bitwise_and(thresh, thresh, mask, mask);

			int total = Core.countNonZero(mask);
			if (total > 200) { // TODO: need to improve this line
				this._code += i + "";
				Scalar scalar = new Scalar(0, 255, 0);
				Imgproc.drawContours(paper, numbers, i, scalar, 2);
			}
		}

		Helper.write("output/" + ScannerMachine.nameFile + "_3_code_result.jpg", paper);
	}
}