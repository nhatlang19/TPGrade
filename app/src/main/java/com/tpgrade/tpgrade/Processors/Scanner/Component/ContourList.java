package com.tpgrade.tpgrade.Processors.Scanner.Component;

import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;
import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class ContourList {
	private List<MatOfPoint> contourList;

	public List<MatOfPoint> getContourList() {
		return this.contourList;
	}

	public ContourList(Edged _edged) {
		Mat edged = _edged.getEdged();

		contourList = new ArrayList<>();
		Imgproc.findContours(edged, contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		// Sort
		Collections.sort(contourList, new Comparator<MatOfPoint>() {
			@Override
			public int compare(MatOfPoint o1, MatOfPoint o2) {
				double area1 = Imgproc.contourArea(o1);
				double area2 = Imgproc.contourArea(o2);

				if (area1 > area2)
					return -1;
				else if (area1 < area2)
					return 1;
				else
					return 0;
			}
		});
	}
}
