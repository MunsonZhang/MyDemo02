package com.example.munson.mydemo02.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NetworkHelper {

    private static final String TAG = "NetworkHelper";

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 根据URL下载图片
     * @param picUrl
     * @return Bitmap
     */
    public static Bitmap downloadBitmapFromUrl(String picUrl){
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(picUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     *
     * @param picUrl
     * @return byte[]
     */
    public static byte[] downloadUrlToStream(String picUrl){
        InputStream in = null;
        ByteArrayOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(picUrl);
            conn.setRequestMethod("GET");
            conn.setReadTimeout(10000);
            conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == 200){
                in = conn.getInputStream();
                out = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = in.read(bytes)) != -1){
                    out.write(bytes, 0 ,length);
                }
                return out.toByteArray();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * url 转 MD5
     * @param url
     * @return
     */
    public static String hashKeyFromUrl(String url){
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = byteToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < bytes.length; i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if(hex.length() == 1){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
