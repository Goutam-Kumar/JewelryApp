package com.android.jewelry.servicehandler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.jewelry.apipresenter.RequestType;
import com.android.jewelry.apphelper.CommonUtil;
import com.android.jewelry.appinstance.JewelryApp;
import com.android.jewelry.dbhelper.JewelryDBHelper;
import com.android.jewelry.dbmodel.CurrentModel;
import com.android.jewelry.dbmodel.DesignModel;
import com.android.jewelry.dbmodel.PartyModel;
import com.android.jewelry.dbmodel.RodiumModel;
import com.android.jewelry.dbmodel.WorkerModel;
import com.android.jewelry.responsemodel.JewelryResponse;
import com.android.jewelry.responsemodel.PartyResponse;
import com.android.jewelry.responsemodel.RodiumResponse;
import com.android.jewelry.responsemodel.WorkerResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackendDataService extends JobService {
    private JewelryDBHelper db;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new JewelryDBHelper(getApplicationContext());
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onStartJob(final JobParameters params) {
        Log.d("DataCapturingService","Job Service started");

        createDirectory();
        //Jewelry list called
        JewelryApp.getInstance().restApi.getAllJewelry().enqueue(new Callback<List<JewelryResponse>>() {
            @Override
            public void onResponse(Call<List<JewelryResponse>> call, Response<List<JewelryResponse>> dictionaryResponse) {
                CommonUtil.printStatus(""+dictionaryResponse.body().toString(), RequestType.LOAD_JEWELRY,dictionaryResponse);
                try {
                    if (dictionaryResponse != null){
                        if (dictionaryResponse.body().size() > 0) {
                            List<JewelryResponse> listOfJewelry = dictionaryResponse.body();
                            for (int i = 0; i < listOfJewelry.size(); i++){
                                JewelryResponse jl = listOfJewelry.get(i);
                                String name = jl.getName();
                                db.addDesignInMainTable(new DesignModel(name, jl.getDesignId(), jl.getCost(), jl.getImage(), "/sdcard/Jewelry/"+name+"downloadedfile.jpg", 1, 0, 0));
                                String subname= name.replaceAll("[^0-9]", "");
                                db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                DesignModel design = db.checkImageInSDCard(name);
                                if (!checkFileExistOrNot(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Jewelry/"+name+"downloadedfile.jpg")){
                                    //Log.e("ASFASFFDSFFF","NEI Download korte hobe>>"+name);
                                    new DownloadFileFromURL().execute(jl.getImage(), name);
                                }else {
                                    //Log.d("efgghhjjtjtj"," Download hogaya>>"+name);
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<JewelryResponse>> call, Throwable t) {
                t.printStackTrace();
                Log.e("DataCapturingService","JobServiceError: "+t.getMessage());
            }
        });
        //Party list called
        JewelryApp.getInstance().restApi.getPartyList().enqueue(new Callback<List<PartyResponse>>() {
            @Override
            public void onResponse(Call<List<PartyResponse>> call, Response<List<PartyResponse>> partyResponse) {
                CommonUtil.printStatus(""+partyResponse.body().toString(), RequestType.LOAD_PARTY,partyResponse);
                try {
                    if (partyResponse != null){
                        if (partyResponse.body().size() > 0) {
                            List<PartyResponse> listOfParty = partyResponse.body();
                            for (int i = 0; i < listOfParty.size(); i++){
                                PartyResponse jl = listOfParty.get(i);
                                db.addParty(new PartyModel(jl.getPartyname()));
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<PartyResponse>> call, Throwable t) {
                t.printStackTrace();
                Log.e("DataCapturingService","Party Response error: "+t.getMessage());
            }
        });

        //Get worker list
        JewelryApp.getInstance().restApi.getWorkerList().enqueue(new Callback<List<WorkerResponse>>() {
            @Override
            public void onResponse(Call<List<WorkerResponse>> call, Response<List<WorkerResponse>> workerResponse) {
                CommonUtil.printStatus(""+workerResponse.body().toString(), RequestType.LOAD_WORKER,workerResponse);
                try {
                    if (workerResponse != null){
                        if (workerResponse.body().size() > 0) {
                            List<WorkerResponse> listOfWorker = workerResponse.body();
                            for (int i = 0; i < listOfWorker.size(); i++){
                                WorkerResponse jl = listOfWorker.get(i);
                                db.addWorker(new WorkerModel(jl.getPhone(), Integer.parseInt(jl.getId()), jl.getName(), jl.getAddress()));
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<WorkerResponse>> call, Throwable t) {
                t.printStackTrace();
                Log.e("DataCapturingService","Worker Response error: "+t.getMessage());
            }
        });
        //Get rodium list
        JewelryApp.getInstance().restApi.getRodiumList().enqueue(new Callback<List<RodiumResponse>>() {
            @Override
            public void onResponse(Call<List<RodiumResponse>> call, Response<List<RodiumResponse>> rodiumResponse) {
                CommonUtil.printStatus(""+rodiumResponse.body().toString(), RequestType.LOAD_RODIUM,rodiumResponse);
                try {
                    if (rodiumResponse != null){
                        if (rodiumResponse.body().size() > 0) {
                            List<RodiumResponse> listOfRodium = rodiumResponse.body();
                            for (int i = 0; i < listOfRodium.size(); i++){
                                RodiumResponse jl = listOfRodium.get(i);
                                db.addRodium(new RodiumModel(jl.getName()));
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<RodiumResponse>> call, Throwable t) {
                t.printStackTrace();
                Log.e("DataCapturingService","Rodium Response error: "+t.getMessage());
            }
        });

        //
        jobFinished(params,false);
        return true;
    }

    private void createDirectory() {
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Jewelry");
        if (!dir.exists()){
            Log.d("DirectoryMGR","Directory created");
            dir.mkdirs();
        }else {
            Log.d("DirectoryMGR","Directory exist");
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private boolean checkFileExistOrNot(String absolutePath){
        try {
            File file = new File(absolutePath);
            if (file.exists())
                return true;
            else
                return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        String dname;
        boolean flag = false;
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            Log.d("####GOUTAM","Downloading"+f_url[1]);
            try {
                dname = f_url[1];
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.setConnectTimeout(1000*2*60);
                conection.setReadTimeout(1000*2*60);
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Jewelry/"+f_url[1]+"downloadedfile.jpg");
                byte data[] = new byte[8192];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
                flag = false;
            }

            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Jewelry/"+f_url[1]+"downloadedfile.jpg";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Complete","Complete");
            if (flag)
                db.updateByName(dname, s);
            //db.updateFirstTime(dname,1);
        }
    }
}
