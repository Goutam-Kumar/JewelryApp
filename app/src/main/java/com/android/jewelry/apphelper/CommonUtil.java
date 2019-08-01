package com.android.jewelry.apphelper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.jewelry.servicehandler.BackendDataService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Response;


public class CommonUtil {
    public static final String INTENT_WORDS = "intent_words";
    public static final int JOB_ID = 3000;
    public static void showToast(Context context, String message){
        if (context != null){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void printStatus(String s, String reqType, Response response) {
        Log.d("STATUS" + " :", s);
        Log.d("STATUS" + " :", reqType);
        Log.d("STATUS" + " :", new Gson().toJson(response.body()));
    }

    public static void scheduleJobForDataCapture(Context context){
        JobScheduler mJobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE );
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, BackendDataService.class));
        builder.setPersisted(true); //persist across device reboots
        builder.setPeriodic(60 * 60 *1000); //run once after 15 min
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // only if wifi avaiblable so they are not using bandwith
        builder.setRequiresDeviceIdle(false); // run not only if the device is idle
        builder.setRequiresCharging(false); // run not only if the device is charging
        int test = mJobScheduler.schedule(builder.build());
        if (test <= 0) {
            Log.e("DataCaptureService","Got Error in JobSchedular");
        }else {
            Log.e("DataCaptureService","JobSchedular working.");
        }
    }

    /*public static String toJson(List<Word> listOfWords){
        Type listType = new TypeToken<List<Word>>() {}.getType();
        Gson gson = new Gson();
        String result = gson.toJson(listOfWords, listType);
        return result;
    }

    public static List<Word> fromJson(String input){
        Type listType = new TypeToken<List<Word>>() {}.getType();
        Gson gson = new Gson();
        List<Word> result = gson.fromJson(input, listType);
        return result;
    }*/
}
