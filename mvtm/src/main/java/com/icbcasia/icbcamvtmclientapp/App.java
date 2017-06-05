package com.icbcasia.icbcamvtmclientapp;

import android.app.Application;

import com.huawei.esdk.cc.MobileCC;

/**
 * Created by Chance on 17/03/27.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 在自己想要开始调试的地方start
//        Debug.startMethodTracing("GithubApp");

        MobileCC.getInstance().initSDK(this);
        MobileCC.getInstance().setLog("eSDK", 1);

        // 在合适的地方stop
//        Debug.stopMethodTracing();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
