package com.tpgrade.tpgrade.Processors.Scanner;

import com.tpgrade.tpgrade.Processors.Scanner.Component.Answer;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Blur;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Code;
import com.tpgrade.tpgrade.Processors.Scanner.Component.ContourList;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Edged;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Gray;
import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;

import java.io.File;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ScannerMachine {
	public static boolean DEBUG = true;
	public static String nameFile = "scanner_machine";
	
	private Mat sourceMat;
	private Gray gray;
	private Blur blurred;
	private Edged edged;
	private ContourList contourList;
	private Code code;
	private Answer answer;
	
	public ScannerMachine(String file) {
		File f = new File(file);
		
		nameFile = f.getName();
		sourceMat = Imgcodecs.imread(file);
	}
	
	public ScannerMachine(Mat mat) {
		sourceMat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);
		mat.copyTo(sourceMat);
	}
	
	public void handle() throws Exception {
		Helper.cleanOutput();
		System.out.println("HANDLE LUAN");
		gray = new Gray(sourceMat);
		blurred = new Blur(gray);
		edged = new Edged(blurred);
		contourList = new ContourList(edged);
		code = new Code(contourList, gray, sourceMat);
		answer = new Answer(contourList, gray, sourceMat);
	}

	public Code getCode() {
		return code;
	}

	public Answer getAnswer() {
		return answer;
	}
}
