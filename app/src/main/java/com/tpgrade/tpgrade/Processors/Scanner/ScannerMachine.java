package com.tpgrade.tpgrade.Processors.Scanner;

import com.tpgrade.tpgrade.Processors.Scanner.Component.Blur;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Code;
import com.tpgrade.tpgrade.Processors.Scanner.Component.ContourList;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Edged;
import com.tpgrade.tpgrade.Processors.Scanner.Component.Gray;
import com.tpgrade.tpgrade.Processors.Scanner.Helper.Helper;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ScannerMachine {
	public static boolean DEBUG = true;
	public static String nameFile = "scanner_machine";
	
	Mat sourceMat;
	Gray gray;
	Blur blurred;
	Edged edged;
	ContourList contourList;
	Code code;
	
	public ScannerMachine(String file) {
		File f = new File(file);
		
		nameFile = f.getName();
		sourceMat = Imgcodecs.imread(file);
	}
	
	public ScannerMachine(Mat mat) {
		mat.copyTo(sourceMat);
	}
	
	public void handle() {
		Helper.cleanOutput();
		
		gray = new Gray(sourceMat);
		blurred = new Blur(gray);
		edged = new Edged(blurred);
		contourList = new ContourList(edged);
		code = new Code(contourList, gray, sourceMat);
		
		System.out.println(code.getCode());
	}
}
