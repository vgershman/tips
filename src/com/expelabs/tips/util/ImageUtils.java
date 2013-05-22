package com.expelabs.tips.util;

import android.graphics.Bitmap;
import android.os.Environment;
import com.expelabs.tips.app.DailyTipsApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 21.05.13
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class ImageUtils {
    public static String writeImageToFile(Bitmap image, String name) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ DailyTipsApp.class.getName();

        String picPath;
        if (image == null) {
            return "";
        }
        try {
            File file = null;
            File dir = new File(dirPath);
            if (dir.exists() || dir.mkdir()) {
                file = new File(dirPath, name);
                if (file.exists()) {
                    file.delete();
                }
            }
            file.createNewFile();
            picPath = file.getPath();
            FileOutputStream outStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (NullPointerException e) {
            picPath = "";
        } catch (IOException e) {
            picPath = "";
        }
        return picPath;
    }

}
