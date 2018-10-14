package com.tpgrade.Lib;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    public static void storePhotoOnDisk(final Bitmap capturedBitmap) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "TPGrade");

                folder.mkdirs();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_SS", Locale.US);

                String format = sdf.format(new Date());

                File photoFile = new File(folder, format.concat(".jpg"));

                if (photoFile.exists()) {
                    photoFile.delete();
                }

                try {
                    FileOutputStream fos = new FileOutputStream(photoFile.getPath());

                    capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    fos.flush();
                    fos.close();
                } catch (java.io.IOException e) {
                    Log.e("PictureDemo", "Exception in photoCallback", e);
                }

            }
        }).start();
    }
}
