package com.example.munson.mydemo02.api;

import android.util.Log;

import com.example.munson.mydemo02.bean.Girl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GirlsApi {
    private static final String TAG = "Network";
    private static final String BASE_URL = "http://gank.io/api/data/福利/";

    public ArrayList<Girl> fetchGirl(int count, int page) {
        String fetchUrl = BASE_URL + count + "/" + page;
        ArrayList<Girl> list = new ArrayList<Girl>();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fetchUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.i(TAG,"Server response: "+code);
            if (code == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data, "UTF-8");
                list = parseGirls(result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return list;
    }

    private ArrayList<Girl> parseGirls(String result) throws JSONException {
        ArrayList<Girl> list = new ArrayList<Girl>();
        JSONObject obj = new JSONObject(result);
        JSONArray arr = obj.getJSONArray("results");
        for(int i=0; i < arr.length(); i++){
            JSONObject object = (JSONObject) arr.get(i);
            Girl girl = new Girl();
            girl.set_id(object.getString("_id"));
            girl.setCreatedAt(object.getString("createdAt"));
            girl.setDesc(object.getString("desc"));
            girl.setSource(object.getString("source"));
            girl.setPublishedAt(object.getString("publishedAt"));
            girl.setType(object.getString("type"));
            girl.setUrl(object.getString("url"));
            girl.setUsed(object.getString("used"));
            girl.setWho(object.getString("who"));
            list.add(girl);
        }
        return list;
    }

    private byte[] readFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        in.close();
        return outputStream.toByteArray();
    }
}
