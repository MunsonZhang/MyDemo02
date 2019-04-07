package com.example.munson.mydemo02.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.example.munson.mydemo02.common.DiskLruCache;
import com.example.munson.mydemo02.common.GirlCompress;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiskCacheHelper {

    private static final String TAG = "DiskCacheHelper";
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private boolean mIsDiskLruCacheCreated = false;

    private Context mContext;
    private GirlCompress mGirlCompress;
    private DiskLruCache mDiskLruCache;
    private static final int DISK_CACHE_INDEX = 0;

    public DiskCacheHelper(Context context){
        this.mContext = context;
        mGirlCompress = new GirlCompress();
        File diskCacheDir = getDiskCacheDir(mContext, "diskCache");
        if (!diskCacheDir.exists()){
            diskCacheDir.mkdir();
        }
        if(getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE){
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long getUsableSpace(File path) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return stats.getBlockSizeLong() * stats.getAvailableBlocksLong();
    }

    private File getDiskCacheDir(Context context, String dirName) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        }else {
            cachePath = context.getCacheDir().getPath();
        }
        Log.d(TAG,"disk cache path: "+cachePath);
        return new File(cachePath + File.separator + dirName);
    }

    /** 根据Key加载磁盘缓存中的图片 */
    public Bitmap loadBitmapFromDiskCache(String key, int reqWidth, int reqHeight)
            throws IOException{
        Log.v(TAG,"加载磁盘缓存中的图片");
        //判断是否在主线程里操作
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能再UI线程中加载图片");
        }
        if(mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        //获取磁盘缓存中的图片，添加到内存缓存中
        DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream)snapShot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mGirlCompress.decodeBitmapFileDescriptor(fileDescriptor,
                    reqWidth, reqHeight);
        }
        return bitmap;
    }

    /** 将图片字节流缓存到磁盘，并返回一个Bitmap用于显示 */
    public Bitmap saveImgByte(String key, int reqWidth, int reqHeight,byte[] bytes) {
        //判断是否在主线程里操作
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能再UI线程里做网络操作！");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream output = editor.newOutputStream(DISK_CACHE_INDEX);
                output.write(bytes);
                output.flush();
                editor.commit();
                output.close();
                return loadBitmapFromDiskCache(key,reqWidth,reqHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DiskLruCache getmDiskLruCache() {
        return mDiskLruCache;
    }

    public boolean getIsDiskCacheCreate() {
        return mIsDiskLruCacheCreated;
    }

    public void setIsDiskCacheCreate(boolean status) {
        this.mIsDiskLruCacheCreated = status;
    }
}
