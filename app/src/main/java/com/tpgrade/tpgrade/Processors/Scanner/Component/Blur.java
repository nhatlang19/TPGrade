package com.tpgrade.tpgrade.Processors.Scanner.Component;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Blur {
	private Mat blurred;
	
	public Mat getBlurred() {
		return blurred;
	}
	
	public Blur(Gray _gray) {
		Mat gray = _gray.getGray();
		
		blurred = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
		Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
	}
}
