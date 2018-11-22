package com.tpgrade.tpgrade.Processors;

import android.graphics.Bitmap;
import android.util.Log;

import com.tpgrade.Lib.FileUtils;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;

public class CaptureImage {
    public static boolean DID_CAPTURE = false;
    public static String doCapture(Mat mat)
    {
        String path = "";
        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bmp);
            path = FileUtils.storePhotoOnDisk(bmp);
            DID_CAPTURE = true;
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }

        return path;
    }
}
