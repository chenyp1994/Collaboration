package com.chenyp.collaboration.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by change on 2015/8/22.
 */
public class ImageUtils {

    public static final String TAG = "ImageUtils";

    public static final long MAX_UPLOAD_IMAGE_SIZE = 300 * 1024;//300K

    public static File compress(String filePath) {
        File file = new File(filePath);
        if (!SDcardUtil.hasExternalStorage()) {
            Log.i(TAG, "SD card is not avaiable/writeable right now.");
            return file;
        }
        String outputPath = Environment.getExternalStorageDirectory() + "/collaboration/" + "compressImg"
                + SimpleDateFormat.getInstance().format(System.currentTimeMillis()) + ".jpg";
        long fileSize = file.length();
        if (fileSize >= MAX_UPLOAD_IMAGE_SIZE) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(outputPath, options);

            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / MAX_UPLOAD_IMAGE_SIZE);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int option = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, option, bos);
            while (bos.toByteArray().length / 1024 > 100) {
                bos.reset();
                option -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, option, bos);
            }
            try {
                FileOutputStream fos = new FileOutputStream(outputPath);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return file;
            }
            return new File(outputPath);
        }
        return file;
    }

    /**
     * 压缩图片
     *
     * @param filePath 图片地址
     * @return file 文件类
     */

    public static File zoomToFix(String filePath) {
        if (!SDcardUtil.hasExternalStorage()) {
            Log.i(TAG, "SD card is not avaiable/writeable right now.");
            return new File(filePath);
        }
        String path = Environment.getExternalStorageDirectory() + "/collaboration/" + "zoomImg"
                + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(path);
        // 文件夹不存在则创建
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
        }
        try {
            Bitmap bitmap = getSmallBitmap(filePath);
            FileOutputStream fos = new FileOutputStream(file); // 保存图片文件
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos);
        } catch (FileNotFoundException e) {
            Log.e("ImageUtils", e.getMessage(), e);
        }
        return file;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

}
