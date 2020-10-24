package com.windinwork.floatingwindowdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FakeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.v("ww",activity.getClass().getName());
                if (activity.getClass().getSimpleName().equals("MainActivity")){

                    ViewGroup contentView= (ViewGroup)activity.getWindow().getDecorView();
                    FloatingWindow fw = new FloatingWindow(activity);
                    fw.setCallBack(new FloatingWindow.CallBack() {
                        @Override
                        public void click(View view) {
                            Toast.makeText(view.getContext(),"click",Toast.LENGTH_SHORT).show();
                        }
                    });
                    contentView.addView(fw, new ViewGroup.LayoutParams(activity.getWindow().getDecorView().getWidth(), activity.getWindow().getDecorView().getHeight()));
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }



}
