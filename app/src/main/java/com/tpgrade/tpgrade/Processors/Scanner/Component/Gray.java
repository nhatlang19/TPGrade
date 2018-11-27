package com.tpgrade.tpgrade.Processors.Scanner.Component;

import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;
import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class Gray {
	private Mat gray;
	
	public Mat getGray() {
		return gray;
	}
	
	public Gray(Mat sourceMat) {
		gray = new Mat(sourceMat.rows(), sourceMat.cols(), CvType.CV_8UC3);
		Imgproc.cvtColor(sourceMat, gray, Imgproc.COLOR_BGR2GRAY);
	}
}
