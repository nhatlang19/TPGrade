package com.tpgrade.tpgrade.Processors.Scanner.Helper;

import com.tpgrade.tpgrade.Processors.Scanner.ScannerMachine;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.List;

public class Helper {
    public static void cleanOutput() {
        File directory = new File("output/");


        if (directory.isDirectory()) {
            // Get all files in directory

            File[] files = directory.listFiles();
            for (File file : files) {
                // Delete each file

                if (!file.delete()) {
                    // Failed to delete file
                    System.out.println("Failed to delete " + file);
                }
            }
        }

    }

    public static void write(String output, Mat source) {
        System.out.println("Writefile: " + output);

        if (ScannerMachine.DEBUG == true) {
            Imgcodecs.imwrite(output, source);
        }
    }

    public static boolean contains(List<Integer> lst, final int key) {
        return lst.contains(key);
    }
}
