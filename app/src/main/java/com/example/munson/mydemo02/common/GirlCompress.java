package com.example.munson.mydemo02.common;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

public class GirlCompress {

    private static final String TAG = "ImageCompress";

    public GirlCompress() {

    }

    /**
     * 压缩资源图片
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeBitmapResource(Resources res, int resId, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = computeSimpleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeBitmapFileDescriptor(FileDescriptor descriptor, int resId, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(descriptor, null, options);

        options.inSampleSize = computeSimpleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(descriptor, null, options);
    }

    /**
     * 计算缩放比例
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int computeSimpleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if(reqWidth == 0 || reqHeight == 0){
            return 1;
        }
        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "图片原始尺寸: "+ width + "x" + height);
        if(height > reqHeight || width > reqWidth){
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "缩小的比例：" + inSampleSize);
        return inSampleSize;
    }
}
