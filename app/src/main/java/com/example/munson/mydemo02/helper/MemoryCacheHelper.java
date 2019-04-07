package com.example.munson.mydemo02.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class MemoryCacheHelper {

    private static final String TAG = "MemoryCacheHelper";
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheHelper(Context mContext){
        this.mContext = mContext;
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public LruCache<String, Bitmap> getMemoryCache(){
        return mMemoryCache;
    }

    public Bitmap getBitmapFromMemoryCache(String key){
        Log.d(TAG,"加载内存里的数据");
        return  mMemoryCache.get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if (null != getBitmapFromMemoryCache(key)){
            mMemoryCache.put(key, bitmap);
        }
    }

}
