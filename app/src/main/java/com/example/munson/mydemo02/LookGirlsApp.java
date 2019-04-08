package com.example.munson.mydemo02;

import android.app.Application;
import android.content.Context;

public class LookGirlsApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static LookGirlsApp getContext(){
        return (LookGirlsApp) context;
    }

}
