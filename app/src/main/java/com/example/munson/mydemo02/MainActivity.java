package com.example.munson.mydemo02;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.munson.mydemo02.api.GirlsApi;
import com.example.munson.mydemo02.bean.Girl;
import com.example.munson.mydemo02.ui.PictureLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> mUrls;
    private Button mNext;
    private Button mRefresh;
    private ImageView mImg;
    private int curPos;

    private GirlsApi mGirlsApi;
    private PictureLoader mPictureLoader;
    private ArrayList<Girl> mGirls;
    private int mPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGirlsApi = new GirlsApi();
        mPictureLoader = new PictureLoader();
        initData();
        initUI();
    }

    private void initUI() {
        mNext = (Button)findViewById(R.id.btn_show);
        mRefresh = (Button)findViewById(R.id.btn_refresh);
        mImg = (ImageView) findViewById(R.id.img_show);
        mNext.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
    }

    private void initData() {
        mGirls = new ArrayList<Girl>();
        new GirlTask(mPage).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show:
                if(mGirls != null && !mGirls.isEmpty()){
                    if(curPos > 9){
                        curPos = 0;
                    }
                    mPictureLoader.load(mImg, mGirls.get(curPos).getUrl());
                }
            case R.id.btn_refresh:
                mPage++;
                new GirlTask(mPage).execute();
                curPos = 0;
                break;
        }
    }

    private class GirlTask extends AsyncTask<Void, Void, ArrayList<Girl>> {

        private int page;

        public GirlTask(int page) {
            this.page = page;
        }

        @Override
        protected ArrayList<Girl> doInBackground(Void... voids) {
            return mGirlsApi.fetchGirl(10,mPage);
        }

        @Override
        protected void onPostExecute(ArrayList<Girl> girls) {
            super.onPostExecute(girls);
            mGirls.clear();
            mGirls.addAll(girls);
        }
    }
}
