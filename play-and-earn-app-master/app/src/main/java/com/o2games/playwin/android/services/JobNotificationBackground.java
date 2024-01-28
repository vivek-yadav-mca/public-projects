package dummydata.android.services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import dummydata.android.Constants;
import dummydata.android.activity.FlipActivity;

import java.util.Date;

public class JobNotificationBackground extends JobService {

    private static final String TAG = "JobNotificationBackground";
    private boolean jobCancelled = false;

    SharedPreferences readSPref;
    FlipActivity flipActivity = new FlipActivity();

    @SuppressLint("LongLogTag")
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {

                Date dateTime = new Date(System.currentTimeMillis());
                long currentTime = dateTime.getTime();

                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);

                    if (i == 5) {
                        flipActivity.sendFreeChanceNotif();
                        Log.d(TAG, "******** notification sent");
                    }

                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private long getSPrefBtnTime() {
        readSPref = getSharedPreferences("" + Constants.USER_SPECIFIC, Context.MODE_PRIVATE);
        long earnBtnTime = readSPref.getLong(Constants.SP_NORMAL_FLIP_EARN_BTN_TIME, 0);
        return earnBtnTime;
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
