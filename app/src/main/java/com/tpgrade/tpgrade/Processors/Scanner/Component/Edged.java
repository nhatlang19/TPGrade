package com.tpgrade.tpgrade.Processors.Scanner.Component;

import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;
import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Edged {
	private Mat edged;
	
	public Mat getEdged() {
		return edged;
	}
	
	public Edged(Blur blur) {
		Mat blurred = blur.getBlurred();
		
		edged = new Mat(blurred.rows(), blurred.cols(), CvType.CV_8UC3);
		Imgproc.Canny(blurred, edged, 75, 200);
		
		Helper.write("output/" + ScannerMachine.nameFile + "_1_edged.jpg", edged);
	}
}
