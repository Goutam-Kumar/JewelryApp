package com.android.jewelry.activity.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.jewelry.BuildConfig;
import com.android.jewelry.R;
import com.android.jewelry.apipresenter.ApiConstants;
import com.android.jewelry.apipresenter.UploadImageInterface;
import com.android.jewelry.apphelper.SharedValue;
import com.android.jewelry.dbhelper.JewelryDBHelper;
import com.android.jewelry.dbmodel.CurrentModel;
import com.android.jewelry.dbmodel.DesignModel;
import com.android.jewelry.dbmodel.PartyModel;
import com.android.jewelry.dbmodel.RodiumModel;
import com.android.jewelry.dbmodel.WorkerModel;
import com.android.jewelry.utils.MyProgressDialog;
import com.android.jewelry.volleyhandler.VolleyRequestHandler;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class GalleryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,IGalleryView{

    @BindView(R.id.rcvGallery)
    RecyclerView rcvGallery;
    private JewelryDBHelper db;
    private List<DesignModel> jewelryList;
    private List<DesignModel> adapterItems = new ArrayList<>();
    private SharedValue sp;
    private int category = 0;
    private ArrayList<Uri> checkedImageUri = new ArrayList<Uri>();
    private ArrayList<String> checkedImageCost = new ArrayList<String>();
    private ArrayList<String> checkedImageName = new ArrayList<String>();
    private GalleryAdapter galleryAdapter;
    private String worker = "";
    private String party_worker = "";
    private String party = "";
    private String workerset2 = "";
    private String workerset3 = "";
    private int priceFrom = 0, priceTo = 0;
    private String cat1 = "";
    private String cat2 = "";
    private String cat3 = "";
    private String rodium_option = "";
    private String rodium_worker = "";
    private String str2by12 = "";
    private String worker2by12 = "";
    private String worker_last = "";
    private String worker_party_name = "";
    private int loc ;
    private MyProgressDialog pDialog;
    private static final String getStockURL = ApiConstants.stockURL;
    private static final String partySearchURL = ApiConstants.partysearchURL;
    private static final String workerreceivedURL = ApiConstants.workerreceiveURL;
    private static final String workerdesignURL = ApiConstants.workerdesignURL;
    private static final String getJewelry = ApiConstants.jewelryURL;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private boolean isStoragePermission = false;
    private boolean isCameraPermission = false;
    File sdImageMainDirectory;
    private Uri mImageUri;
    private Uri mServerUri = null;
    private String savedNewURI = null;
    DesignModel updateDesign = null;
    AlertDialog chooserDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sp = new SharedValue(GalleryActivity.this);
        db = new JewelryDBHelper(this);
        jewelryList = db.getAllFace();
        for (DesignModel f : jewelryList) {
            final String path = f.getDesignUri();
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            adapterItems.add(f);
        }
        galleryAdapter = new GalleryAdapter(GalleryActivity.this,adapterItems,this);
        rcvGallery.setLayoutManager(new GridLayoutManager(this,2));
        rcvGallery.setAdapter(galleryAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                category = sp.getCategoryPref();
                checkedImageUri = galleryAdapter.getCheckedImageUri();
                checkedImageCost = galleryAdapter.getCheckedImageCost();
                checkedImageName = galleryAdapter.getCheckedImageName();
                if (category == 0) {
                    if (checkedImageUri.size() > 0 && checkedImageUri.size() <= 30) {
                        Intent shareIntent = new Intent();
                        //shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.setPackage("com.whatsapp");
                        Log.e("Image Path", "" + checkedImageUri.get(0));
                        ArrayList<Uri> list = new ArrayList<Uri>();
                        for (int i = 0; i < checkedImageUri.size(); i++) {
                            //File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + checkedImageUri.get(i));
                            File f = new File(checkedImageUri.get(i).toString());
                            Log.e("FileURI", ""+checkedImageUri.get(i));
                            //Uri shareUri = Uri.fromFile(f);
                            Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".provider", f);
                            list.add(shareUri);
                        }
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    } else if (checkedImageUri.size() > 30) {
                        Toast.makeText(GalleryActivity.this, "Sorry! You can't share more than 30 images at a time.", Toast.LENGTH_LONG).show();
                    }
                } else if (category == 1) {
                    if (checkedImageUri.size() > 0 && checkedImageUri.size() <= 30) {
                        deleteFiles(Environment.getExternalStorageDirectory().toString() + "/Jewelry/Share/");
                        final ArrayList<Uri> list = new ArrayList<Uri>();
                        Toast.makeText(GalleryActivity.this,""+checkedImageUri.size(),Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < checkedImageUri.size(); i++) {
                            File file = null;
                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeFile(checkedImageUri.get(i).toString(), options);
                                Bitmap b = mark(bitmap, "S:No-" + checkedImageCost.get(i));
                                Log.e("Costing?????",checkedImageCost.get(i));
                                file = Environment.getExternalStorageDirectory();
                                File dir = new File(file.getAbsolutePath() + "/Jewelry/Share");
                                dir.mkdirs();

                                File filePath = new File(Environment.getExternalStorageDirectory().toString() + "/Jewelry/Share/");
                                File image = new File(filePath, "file" + i + "share.jpg");
                                FileOutputStream outStream;
                                outStream = new FileOutputStream(image);
                                b.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                                outStream.flush();
                                outStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            File f = new File(file.getAbsolutePath() + "/Jewelry/Share/" + "file" + i + "share.jpg");
                            Uri shareUri = Uri.fromFile(f);
                            list.add(shareUri);
                        }


                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setPackage("com.whatsapp");
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                        }
                        //deleteFiles(Environment.getExternalStorageDirectory().toString() + "/Jewelry/Share/");


                    } else if (checkedImageUri.size() > 0) {
                        Toast.makeText(GalleryActivity.this, "Sorry! You can't share more than 30 images at a time.", Toast.LENGTH_LONG).show();
                    }
                }//Add price seperately
                else if (category == 2){
                    if (checkedImageUri.size() > 0 && checkedImageUri.size() <= 30) {
                        String caption = "";
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        shareIntent.setPackage("com.whatsapp");
                        Log.e("Image Path", "" + checkedImageUri.get(0));
                        ArrayList<Uri> list = new ArrayList<Uri>();
                        ArrayList<CharSequence> captionList = new ArrayList<CharSequence>();
                        for (int i = 0; i < checkedImageUri.size(); i++) {
                            File f = new File(checkedImageUri.get(i).toString());
                            Log.e("FileURI", ""+checkedImageUri.get(i));
                            Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".provider", f);
                            list.add(shareUri);
                        }
                        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
                        for (int i=0;i<checkedImageCost.size();i++){
                            caption = caption +" " + checkedImageName.get(i) + "=>" + checkedImageCost.get(i) +",";
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "S:No-"+checkedImageCost.get(i));
                        }
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        try {
                            startActivity(shareIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                        }
                    } else if (checkedImageUri.size() > 30) {
                        Toast.makeText(GalleryActivity.this, "Sorry! You can't share more than 30 images at a time.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        //int x = 7/0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkedImageCost.clear();
        checkedImageUri.clear();
        checkedImageName.clear();
        adapterItems.clear();
        //jewelryList = db.getAllFace();
        for (DesignModel f : jewelryList) {
            final String path = f.getDesignUri();
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            adapterItems.add(f);
        }

        for (int i = 0; i < adapterItems.size(); i++) {
            adapterItems.get(i).setSelected(0);
        }

        rcvGallery.setLayoutManager(new GridLayoutManager(this, 2));
        galleryAdapter = new GalleryAdapter(GalleryActivity.this,adapterItems,this);
        rcvGallery.setAdapter(galleryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int pos = new SharedValue(GalleryActivity.this).getScrollPosition();
        rcvGallery.getLayoutManager().scrollToPosition(pos);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
                Log.e("All deleted#####","Deleted");
            } catch (IOException e) {
            }
        }
    }

    private Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize(55);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        paint.setUnderlineText(true);
        canvas.drawText(watermark, 100, 250, paint);
        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#FFFF33"));
        paint2.setTextSize(55);
        paint2.setFakeBoldText(true);
        paint2.setAntiAlias(true);
        paint2.setUnderlineText(true);
        paint2.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(GalleryActivity.this.getString(R.string.tnc), 600, 100  , paint2);
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            startActivity(new Intent(GalleryActivity.this,GalleryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            reloadGallery();
        } else if (id == R.id.nav_search) {
            searchJewelry();
        } else if (id == R.id.nav_settings) {
            filterDesign();
        } else if (id == R.id.nav_filter) {
            settingsApp();
        } else if (id == R.id.nav_price) {
            priceSearch();
        } else if (id == R.id.nav_sync) {
            syncJewelry();
        }else if (id == R.id.name_search) {
            nameSearch();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void reloadGallery() {

        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                getApplicationContext(),
                0,
                getJewelry,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        try {
                            JSONArray userArray = new JSONArray(result);
                            if (userArray.length() > 0) {
                                Log.e("Main Table",""+getJewelry);
                                for (int i = 0; i < userArray.length(); i++){
                                    JSONObject jewelry = userArray.getJSONObject(i);
                                    String name = jewelry.optString("name");
                                    String design_id = jewelry.optString("design_id");
                                    String cost = jewelry.optString("cost");
                                    String image = jewelry.optString("image");
                                    Log.e("design_id", jewelry.optString("design_id"));
                                    String subname= name.replaceAll("[^0-9]", "");
                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                }

                                adapterItems.clear();
                                jewelryList = db.getAllFace();
                                Log.e("Count object", "" + jewelryList.size());
                                for (DesignModel f : jewelryList) {
                                    final String path = f.getDesignUri();
                                    File file = new File(path);
                                    Uri uri = Uri.fromFile(file);
                                    adapterItems.add(f);
                                }

                                for (int i = 0; i < adapterItems.size(); i++) {
                                    adapterItems.get(i).setSelected(0);
                                }
                                galleryAdapter.notifyDataSetChanged();
                                //counter = 0;
                                //labelUpdate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(String fail) {
                        VolleyLog.e("", fail);
                    }
                }
        );
        streq.setRetryPolicy(new DefaultRetryPolicy(
                ApiConstants.TIMEOUT_MS,
                ApiConstants.DEFAULT_RETRY,
                ApiConstants.FLOAT_FRACTION
        ));
        Volley.newRequestQueue(getApplicationContext()).add(streq);
    }

    private void settingsApp() {
        List<WorkerModel> workerList = db.getAllWorkers();
        List<PartyModel> partyList = db.getAllParty();
        List<RodiumModel> rodiumList = db.getAllRodium();

        final ArrayList<String> workerar = new ArrayList<String>();
        ArrayList<String> partyarr = new ArrayList<String>();
        ArrayList<String> rodiumarr = new ArrayList<String>();
        ArrayList<String> list2by12 = new ArrayList<String>();

        workerar.add("All Worker");
        for (int i = 0; i < workerList.size(); i++) {
            workerar.add(workerList.get(i).getName());
        }
        for (int i = 0; i < partyList.size(); i++) {
            partyarr.add(partyList.get(i).getPartyName());
        }
        for (int i = 0; i < rodiumList.size(); i++) {
            rodiumarr.add(rodiumList.get(i).getName());
        }

        list2by12.add("2");
        list2by12.add("12");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = GalleryActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_window, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Select Filter Category");
        RadioGroup radiogroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
        final RadioButton btn1 = (RadioButton) dialogView.findViewById(R.id.btn1);
        final RadioButton btn2 = (RadioButton) dialogView.findViewById(R.id.btn2);
        final RadioButton btn3 = (RadioButton) dialogView.findViewById(R.id.btn3);
        final RadioButton btn4 = (RadioButton) dialogView.findViewById(R.id.btn4);
        final RadioButton btn5 = (RadioButton) dialogView.findViewById(R.id.btn5);
        final RadioButton btn6 = (RadioButton) dialogView.findViewById(R.id.btn6);
        final RadioButton btn7 = (RadioButton) dialogView.findViewById(R.id.btn7);

        final LinearLayout lin_worker = (LinearLayout) dialogView.findViewById(R.id.lin_worker);
        final LinearLayout lin_party = (LinearLayout) dialogView.findViewById(R.id.lin_party);
        final LinearLayout lin_wokerorder = (LinearLayout) dialogView.findViewById(R.id.lin_wokerorder);
        final LinearLayout lin_woker = (LinearLayout) dialogView.findViewById(R.id.lin_woker);
        final LinearLayout lin_rodium_woker = (LinearLayout) dialogView.findViewById(R.id.lin_rodium_woker);
        final LinearLayout lin_2by12 = (LinearLayout) dialogView.findViewById(R.id.lin_2by12);
        final LinearLayout lin_worker_party = (LinearLayout) dialogView.findViewById(R.id.lin_worker_party);

        Spinner spinner_wrkr = (Spinner) dialogView.findViewById(R.id.spinner_wrkr);
        Spinner spinner_category1 = (Spinner) dialogView.findViewById(R.id.spinner_category1);
        //Spinner spinner_prty = (Spinner) dialogView.findViewById(R.id.spinner_prty);
        final EditText partyname = (EditText) dialogView.findViewById(R.id.partyname);
        Spinner spinner_prty_worker = (Spinner) dialogView.findViewById(R.id.spinner_prty_worker);
        Spinner spinner_category2 = (Spinner) dialogView.findViewById(R.id.spinner_category2);
        Spinner spinner_worker = (Spinner) dialogView.findViewById(R.id.spinner_worker);
        Spinner spinner_category3 = (Spinner) dialogView.findViewById(R.id.spinner_category3);
        Spinner spinner_woker = (Spinner) dialogView.findViewById(R.id.spinner_woker);
        Spinner spinner_option = (Spinner) dialogView.findViewById(R.id.spinner_option);
        Spinner spinner_rodium = (Spinner) dialogView.findViewById(R.id.spinner_rodium);
        Spinner spinner_2by12 = (Spinner) dialogView.findViewById(R.id.spinner_2by12);
        Spinner spinner_worker_2by12 = (Spinner) dialogView.findViewById(R.id.spinner_worker_2by12);
        final Spinner spinner_worker_party = (Spinner) dialogView.findViewById(R.id.spinner_worker_party);
        final Spinner spinner_partylist = (Spinner) dialogView.findViewById(R.id.spinner_partylist);
        //final EditText et_rodium_worker_name = (EditText) dialogView.findViewById(R.id.et_rodium_worker_name);
        Button button = (Button) dialogView.findViewById(R.id.button);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_wrkr.setAdapter(arrayAdapter);

        String[] category1 = getResources().getStringArray(R.array.category1);
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, category1);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category1.setAdapter(arrayAdapter3);

        ArrayAdapter<String> arrayAdapter8 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prty_worker.setAdapter(arrayAdapter8);

        String[] category2 = getResources().getStringArray(R.array.category2);
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, category2);
        arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category2.setAdapter(arrayAdapter4);

        ArrayAdapter<String> arrayAdapter6 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_worker.setAdapter(arrayAdapter6);

        String[] category3 = getResources().getStringArray(R.array.category2);
        ArrayAdapter<String> arrayAdapter7 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, category3);
        arrayAdapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category3.setAdapter(arrayAdapter7);

        ArrayAdapter<String> arrayAdapter5 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_woker.setAdapter(arrayAdapter5);

        final String[] rodium_options = getResources().getStringArray(R.array.rodium_option);
        ArrayAdapter<String> arrayAdapter9 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, rodium_options);
        arrayAdapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_option.setAdapter(arrayAdapter9);

        ArrayAdapter<String> arrayAdapter10 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, rodiumarr);
        arrayAdapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rodium.setAdapter(arrayAdapter10);

        ArrayAdapter<String> arrayAdapter2by12 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, list2by12);
        arrayAdapter2by12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_2by12.setAdapter(arrayAdapter2by12);

        ArrayAdapter<String> arrayAdapterworker2by12 = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapterworker2by12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_worker_2by12.setAdapter(arrayAdapterworker2by12);

        final ArrayAdapter<String> arrayAdapterworkerlast = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, workerar);
        arrayAdapterworkerlast.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_worker_party.setAdapter(arrayAdapterworkerlast);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        spinner_wrkr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    worker = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    worker = arr[0];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                worker = "0";
            }
        });

        spinner_category1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat1 = "" + position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cat1 = "0";
            }
        });

        spinner_prty_worker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    party_worker = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    party_worker = arr[0];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                worker = "0";
            }
        });

        spinner_category2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cat2 = "" + position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cat2 = "0";
            }
        });

        spinner_worker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    workerset2 = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    workerset2 = arr[0];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                workerset2 = "0";
            }
        });

        spinner_category3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    cat3 = "6";
                }else {
                    cat3 = "" + position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cat3 = "0";
            }
        });


        spinner_woker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    workerset3 = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    workerset3 = arr[0];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                workerset3 = "0";
            }
        });

        spinner_option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    rodium_option = "0";
                }else if (position == 1){
                    rodium_option = "1";
                }else if (position == 2){
                    rodium_option = "3";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_rodium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rodium_worker = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_2by12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str2by12 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_worker_2by12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //worker2by12 = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    worker2by12 = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    worker2by12 = arr[0];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_partylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                worker_party_name = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_worker_party.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //worker_last = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    worker_last = "0";
                } else {
                    String[] arr = parent.getItemAtPosition(position).toString().split("\\s+");
                    worker_last = arr[0];
                }
                pDialog = new MyProgressDialog(GalleryActivity.this);
                pDialog.setMessage("Please Wait..");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
                final ArrayList<String> partyList = new ArrayList<String>();
                StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                        getApplicationContext(),
                        0,
                        "http://jewelrysoft.in/adminapi/viewworkerparty/"+worker_last,
                        new VolleyRequestHandler.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Log.e("Filter Result", "http://jewelrysoft.in/adminapi/viewworkerparty/"+worker_last);

                                try {
                                    JSONArray userArray = new JSONArray(result);
                                    if (userArray.length() > 0) {
                                        Log.e("RESULT", "" + userArray.length());
                                        for (int i = 0; i < userArray.length(); i++) {
                                            JSONObject jewelry = userArray.getJSONObject(i);
                                            String name = jewelry.optString("partyname");
                                            partyList.add(name);
                                        }
                                        ArrayAdapter<String> arrayAdapterpartylist = new ArrayAdapter<String>(GalleryActivity.this, android.R.layout.simple_spinner_item, partyList);
                                        arrayAdapterpartylist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner_partylist.setAdapter(arrayAdapterpartylist);
                                    }else {
                                        //Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(String fail) {
                                Log.e("FDSFF", fail);
                                if (pDialog.isShowing()){
                                    pDialog.dismiss();
                                }
                            }
                        }
                );
                streq.setRetryPolicy(new DefaultRetryPolicy(15000,
                        5,
                        1.0f));
                Volley.newRequestQueue(getApplicationContext()).add(streq);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btn1) {
                    lin_worker.setVisibility(View.VISIBLE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn2) {
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.VISIBLE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn3) {
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.VISIBLE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn4){
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.VISIBLE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn5){
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.VISIBLE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if(checkedId == R.id.btn6){
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.VISIBLE);
                    lin_worker_party.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn7){
                    lin_worker.setVisibility(View.GONE);
                    lin_party.setVisibility(View.GONE);
                    lin_wokerorder.setVisibility(View.GONE);
                    lin_woker.setVisibility(View.GONE);
                    lin_rodium_woker.setVisibility(View.GONE);
                    lin_2by12.setVisibility(View.GONE);
                    lin_worker_party.setVisibility(View.VISIBLE);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.dropCurrent();
                //for Only Radio Button 1
                if (btn1.isChecked()){

                    pDialog = new MyProgressDialog(GalleryActivity.this);
                    pDialog.setMessage("Please Wait..");
                    pDialog.setCancelable(false);
                    pDialog.setIndeterminate(true);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.show();
                    StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                            getApplicationContext(),
                            0,
                            getStockURL + cat1 + "_" + worker,
                            new VolleyRequestHandler.VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    Log.e("Filter Result", getStockURL + cat1+"_" + worker);

                                    try {
                                        JSONArray userArray = new JSONArray(result);
                                        if (userArray.length() > 0) {
                                            Log.e("RESULT", "" + userArray.length());
                                            for (int i = 0; i < userArray.length(); i++) {
                                                JSONObject jewelry = userArray.getJSONObject(i);
                                                String name = jewelry.optString("design_name");
                                                String subname= name.replaceAll("[^0-9]", "");
                                                db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                            }
                                            adapterItems.clear();
                                            jewelryList = db.getAllFace();
                                            Log.e("Count object", "" + jewelryList.size());
                                            for (DesignModel f : jewelryList) {
                                                final String path = f.getDesignUri();
                                                File file = new File(path);
                                                Uri uri = Uri.fromFile(file);
                                                adapterItems.add(f);

                                            }

                                            for (int i = 0; i < adapterItems.size(); i++) {
                                                adapterItems.get(i).setSelected(0);
                                            }
                                            galleryAdapter.notifyDataSetChanged();
                                            //counter = 0;
                                            //labelUpdate();
                                        }else {
                                            Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(String fail) {
                                    Log.e("FDSFF", fail);
                                    if (pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                }
                            }
                    );
                    streq.setRetryPolicy(new DefaultRetryPolicy(15000,
                            5,
                            1.0f));
                    Volley.newRequestQueue(getApplicationContext()).add(streq);
                }
                //For Radio Button2
                else if (btn2.isChecked()){
                    party = partyname.getText().toString();
                    if (TextUtils.isEmpty(party)){
                        Toast.makeText(GalleryActivity.this,"Please Enter a Party Name",Toast.LENGTH_LONG).show();
                    }else if (TextUtils.isEmpty(party_worker)){
                        Toast.makeText(GalleryActivity.this,"Please select a Worker",Toast.LENGTH_LONG).show();
                    }
                    else {
                        pDialog = new MyProgressDialog(GalleryActivity.this);
                        pDialog.setMessage("Please Wait..");
                        pDialog.setCancelable(false);
                        pDialog.setIndeterminate(true);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.show();
                        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                                getApplicationContext(),
                                0,
                                partySearchURL + party + "/" + party_worker + "/" + cat2,
                                new VolleyRequestHandler.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Log.e("Filter Result", partySearchURL + party + "/" + cat2);

                                        try {
                                            JSONArray userArray = new JSONArray(result);
                                            if (userArray.length() > 0) {
                                                Log.e("RESULT", "" + userArray.length());
                                                for (int i = 0; i < userArray.length(); i++) {
                                                    JSONObject jewelry = userArray.getJSONObject(i);
                                                    String name = jewelry.optString("name");
                                                    String subname= name.replaceAll("[^0-9]", "");
                                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                                }

                                                adapterItems.clear();
                                                jewelryList = db.getAllFace();
                                                Log.e("Count object", "" + jewelryList.size());
                                                for (DesignModel f : jewelryList) {
                                                    final String path = f.getDesignUri();
                                                    File file = new File(path);
                                                    Uri uri = Uri.fromFile(file);
                                                    adapterItems.add(f);
                                                }

                                                for (int i = 0; i < adapterItems.size(); i++) {
                                                    adapterItems.get(i).setSelected(0);
                                                }
                                                galleryAdapter.notifyDataSetChanged();
                                                //counter = 0;
                                                //labelUpdate();
                                            }else {
                                                Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String fail) {
                                        Log.e("FDSFF", fail);
                                        Toast.makeText(GalleryActivity.this,"Some Error happen.",Toast.LENGTH_LONG).show();
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                        );
                        streq.setRetryPolicy(new DefaultRetryPolicy(
                                ApiConstants.TIMEOUT_MS,
                                ApiConstants.DEFAULT_RETRY,
                                ApiConstants.FLOAT_FRACTION
                        ));
                        Volley.newRequestQueue(getApplicationContext()).add(streq);
                    }
                }
                //For button 3
                else if (btn3.isChecked()){
                    if (TextUtils.isEmpty(workerset2)){
                        Toast.makeText(GalleryActivity.this,"Select a worker",Toast.LENGTH_LONG).show();
                    }else if (TextUtils.isEmpty(cat3)){
                        Toast.makeText(GalleryActivity.this,"Select category",Toast.LENGTH_LONG).show();
                    }else {

                        pDialog = new MyProgressDialog(GalleryActivity.this);
                        pDialog.setMessage("Please Wait..");
                        pDialog.setCancelable(false);
                        pDialog.setIndeterminate(true);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.show();

                        StringRequest streq1 = new VolleyRequestHandler().GeneralVolleyRequest(
                                getApplicationContext(),
                                0,
                                workerreceivedURL + cat3 + "_" + workerset2,
                                new VolleyRequestHandler.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        Log.e("Filter Result", workerreceivedURL + cat3 + "_" + workerset2);

                                        try {
                                            JSONArray userArray = new JSONArray(result);
                                            if (userArray.length() > 0) {
                                                Log.e("RESULT", "" + userArray.length());
                                                for (int i = 0; i < userArray.length(); i++) {
                                                    JSONObject jewelry = userArray.getJSONObject(i);
                                                    String name = jewelry.optString("design_name");
                                                    String subname= name.replaceAll("[^0-9]", "");
                                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                                }

                                                adapterItems.clear();
                                                jewelryList = db.getAllFace();
                                                Log.e("Count object", "" + jewelryList.size());
                                                for (DesignModel f : jewelryList) {
                                                    final String path = f.getDesignUri();
                                                    File file = new File(path);
                                                    Uri uri = Uri.fromFile(file);
                                                    adapterItems.add(f);
                                                }

                                                for (int i = 0; i < adapterItems.size(); i++) {
                                                    adapterItems.get(i).setSelected(0);
                                                }
                                                galleryAdapter.notifyDataSetChanged();
                                                //counter = 0;
                                                //labelUpdate();
                                            }else {
                                                Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String fail) {
                                        Log.e("FDSFF", fail);
                                        Toast.makeText(GalleryActivity.this,"Some Error happen.",Toast.LENGTH_LONG).show();
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                        );
                        streq1.setRetryPolicy(new DefaultRetryPolicy(
                                ApiConstants.TIMEOUT_MS,
                                ApiConstants.DEFAULT_RETRY,
                                ApiConstants.FLOAT_FRACTION
                        ));
                        Volley.newRequestQueue(getApplicationContext()).add(streq1);
                    }
                }
                //for Button 4
                else if (btn4.isChecked()){
                    if (TextUtils.isEmpty(workerset3)){
                        Toast.makeText(GalleryActivity.this,"Select a worker",Toast.LENGTH_LONG).show();
                    } else{

                        pDialog = new MyProgressDialog(GalleryActivity.this);
                        pDialog.setMessage("Please Wait..");
                        pDialog.setCancelable(false);
                        pDialog.setIndeterminate(true);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.show();

                        StringRequest streq1 = new VolleyRequestHandler().GeneralVolleyRequest(
                                getApplicationContext(),
                                0,
                                workerdesignURL + workerset3,
                                new VolleyRequestHandler.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        Log.e("Filter Result", workerdesignURL + workerset3);

                                        try {
                                            JSONArray userArray = new JSONArray(result);
                                            if (userArray.length() > 0) {
                                                Log.e("RESULT", "" + userArray.length());
                                                for (int i = 0; i < userArray.length(); i++) {
                                                    JSONObject jewelry = userArray.getJSONObject(i);
                                                    String name = jewelry.optString("name");
                                                    String subname= name.replaceAll("[^0-9]", "");
                                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                                }

                                                adapterItems.clear();
                                                jewelryList = db.getAllFace();
                                                Log.e("Count object", "" + jewelryList.size());
                                                for (DesignModel f : jewelryList) {
                                                    final String path = f.getDesignUri();
                                                    File file = new File(path);
                                                    Uri uri = Uri.fromFile(file);
                                                    adapterItems.add(f);
                                                }

                                                for (int i = 0; i < adapterItems.size(); i++) {
                                                    adapterItems.get(i).setSelected(0);
                                                }
                                                galleryAdapter.notifyDataSetChanged();
                                                //counter = 0;
                                                //labelUpdate();
                                            }else {
                                                Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String fail) {
                                        Log.e("FDSFF", fail);
                                        Toast.makeText(GalleryActivity.this,"Some Error happen.",Toast.LENGTH_LONG).show();
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                        );
                        streq1.setRetryPolicy(new DefaultRetryPolicy(
                                ApiConstants.TIMEOUT_MS,
                                ApiConstants.DEFAULT_RETRY,
                                ApiConstants.FLOAT_FRACTION
                        ));
                        Volley.newRequestQueue(getApplicationContext()).add(streq1);
                    }
                }

                //for Button 5
                else if (btn5.isChecked()){
                    if (TextUtils.isEmpty(rodium_option)){
                        Toast.makeText(GalleryActivity.this,"Select an option",Toast.LENGTH_LONG).show();
                    }else if (TextUtils.isEmpty(rodium_worker)){
                        Toast.makeText(GalleryActivity.this,"Select Rodium Name",Toast.LENGTH_LONG).show();
                    }
                    else{

                        pDialog = new MyProgressDialog(GalleryActivity.this);
                        pDialog.setMessage("Please Wait..");
                        pDialog.setCancelable(false);
                        pDialog.setIndeterminate(true);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.show();

                        StringRequest streq1 = new VolleyRequestHandler().GeneralVolleyRequest(
                                getApplicationContext(),
                                0,
                                "http://jewelrysoft.in/adminapi/viewrodiumordersbytypeapi/"+rodium_option+"/"+ rodium_worker,
                                new VolleyRequestHandler.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {

                                        // Log.e("Filter Result", workerdesignURL + workerset3);
                                        Log.e("Response>>>>",result);
                                        try {
                                            JSONArray userArray = new JSONArray(result);
                                            if (userArray.length() > 0) {
                                                Log.e("RESULT", "" + userArray.length());
                                                for (int i = 0; i < userArray.length(); i++) {
                                                    JSONObject jewelry = userArray.getJSONObject(i);
                                                    String name = jewelry.optString("odesign_name");
                                                    String subname= name.replaceAll("[^0-9]", "");
                                                    Log.e("Sub Name>>>>",subname);
                                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                                }

                                                adapterItems.clear();
                                                jewelryList = db.getAllFace();
                                                Log.e("Count object", "" + jewelryList.size());
                                                for (DesignModel f : jewelryList) {
                                                    final String path = f.getDesignUri();
                                                    File file = new File(path);
                                                    Uri uri = Uri.fromFile(file);
                                                    adapterItems.add(f);
                                                }

                                                for (int i = 0; i < adapterItems.size(); i++) {
                                                    adapterItems.get(i).setSelected(0);
                                                }
                                                galleryAdapter.notifyDataSetChanged();
                                                //counter = 0;
                                                //labelUpdate();
                                            }else {
                                                Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String fail) {
                                        Log.e("FDSFF", fail);
                                        Toast.makeText(GalleryActivity.this,"Some Error happen.",Toast.LENGTH_LONG).show();
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                        );
                        streq1.setRetryPolicy(new DefaultRetryPolicy(
                                ApiConstants.TIMEOUT_MS,
                                ApiConstants.DEFAULT_RETRY,
                                ApiConstants.FLOAT_FRACTION
                        ));
                        Volley.newRequestQueue(getApplicationContext()).add(streq1);
                    }
                } else if (btn6.isChecked()){

                    pDialog = new MyProgressDialog(GalleryActivity.this);
                    pDialog.setMessage("Please Wait..");
                    pDialog.setCancelable(false);
                    pDialog.setIndeterminate(true);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.show();

                    StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                            getApplicationContext(),
                            0,
                            "http://jewelrysoft.in/adminapi/viewdesign2and12/"+str2by12+"/"+worker2by12,
                            new VolleyRequestHandler.VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    Log.e("Filter Result", "http://jewelrysoft.in/adminapi/viewdesign2and12/"+str2by12+"/"+worker2by12);

                                    try {
                                        JSONArray userArray = new JSONArray(result);
                                        if (userArray.length() > 0) {
                                            Log.e("RESULT", "" + userArray.length());
                                            for (int i = 0; i < userArray.length(); i++) {
                                                JSONObject jewelry = userArray.getJSONObject(i);
                                                String name = jewelry.optString("design_name");
                                                String subname= name.replaceAll("[^0-9]", "");
                                                db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                            }

                                            adapterItems.clear();
                                            jewelryList = db.getAllFace();
                                            Log.e("Count object", "" + jewelryList.size());
                                            for (DesignModel f : jewelryList) {
                                                final String path = f.getDesignUri();
                                                File file = new File(path);
                                                Uri uri = Uri.fromFile(file);
                                                adapterItems.add(f);
                                            }

                                            for (int i = 0; i < adapterItems.size(); i++) {
                                                adapterItems.get(i).setSelected(0);
                                            }
                                            galleryAdapter.notifyDataSetChanged();
                                            //counter = 0;
                                            //labelUpdate();
                                        }else {
                                            Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(String fail) {
                                    Log.e("FDSFF", fail);
                                    if (pDialog.isShowing()){
                                        pDialog.dismiss();
                                    }
                                }
                            }
                    );
                    streq.setRetryPolicy(new DefaultRetryPolicy(15000,
                            5,
                            1.0f));
                    Volley.newRequestQueue(getApplicationContext()).add(streq);
                }else if (btn7.isChecked()){
                    if(!TextUtils.isEmpty(worker_party_name)){
                        pDialog = new MyProgressDialog(GalleryActivity.this);
                        pDialog.setMessage("Please Wait..");
                        pDialog.setCancelable(false);
                        pDialog.setIndeterminate(true);
                        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pDialog.show();


                        final String[] workername = worker_last.split("\\s+");
                        //http://jewelrysoft.in/adminapi/viewdesignparty/SANJIT/K24
                        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                                getApplicationContext(),
                                0,
                                "http://jewelrysoft.in/adminapi/viewdesignparty/"+workername[0]+"/"+worker_party_name,
                                new VolleyRequestHandler.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Log.e("Filter Result", "http://jewelrysoft.in/adminapi/viewdesignparty/"+workername[0]+"/"+worker_party_name);

                                        try {
                                            JSONArray userArray = new JSONArray(result);
                                            if (userArray.length() > 0) {
                                                Log.e("RESULT", "" + userArray.length());
                                                for (int i = 0; i < userArray.length(); i++) {
                                                    JSONObject jewelry = userArray.getJSONObject(i);
                                                    String name = jewelry.optString("design_name");
                                                    String subname= name.replaceAll("[^0-9]", "");
                                                    db.addCurrent(new CurrentModel(name,Integer.parseInt(subname)));
                                                }

                                                adapterItems.clear();
                                                jewelryList = db.getAllFace();
                                                Log.e("Count object", "" + jewelryList.size());
                                                for (DesignModel f : jewelryList) {
                                                    final String path = f.getDesignUri();
                                                    File file = new File(path);
                                                    Uri uri = Uri.fromFile(file);
                                                    adapterItems.add(f);
                                                }

                                                for (int i = 0; i < adapterItems.size(); i++) {
                                                    adapterItems.get(i).setSelected(0);
                                                }
                                                galleryAdapter.notifyDataSetChanged();
                                                //counter = 0;
                                                //labelUpdate();
                                            }else {
                                                Toast.makeText(GalleryActivity.this,"No Design Found",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(String fail) {
                                        Log.e("FDSFF", fail);
                                        if (pDialog.isShowing()){
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                        );
                        streq.setRetryPolicy(new DefaultRetryPolicy(15000,
                                5,
                                1.0f));
                        Volley.newRequestQueue(getApplicationContext()).add(streq);
                    }
                }
                alertDialog.dismiss();
            }
        });
    }

    private void nameSearch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = GalleryActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.name_sort_window, null);
        final EditText et_from_name = (EditText) dialogView.findViewById(R.id.et_from_name);
        final EditText et_to_name = (EditText) dialogView.findViewById(R.id.et_to_name);
        Button btn_search = (Button) dialogView.findViewById(R.id.btn_search);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Filter with Name Range");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DesignModel> designs = new ArrayList<DesignModel>();
                alertDialog.dismiss();
                adapterItems.clear();
                if (TextUtils.isEmpty(et_from_name.getText().toString())){
                    Toast.makeText(GalleryActivity.this,"Enter name from",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(et_to_name.getText().toString())){
                    Toast.makeText(GalleryActivity.this,"Enter name to",Toast.LENGTH_LONG).show();
                }else {

                    int fromName = Integer.parseInt(et_from_name.getText().toString().replaceAll("[^0-9]", ""));
                    int toName = Integer.parseInt(et_to_name.getText().toString().replaceAll("[^0-9]", ""));
                    jewelryList = db.getAllSortedNamedImages(fromName,toName);
                    if (jewelryList.size() > 0){
                        for (DesignModel f : jewelryList) {
                            final String path = f.getDesignUri();
                            File file = new File(path);
                            Uri uri = Uri.fromFile(file);
                            adapterItems.add(f);
                        }
                        galleryAdapter.notifyDataSetChanged();
                        //counter = 0;
                        //labelUpdate();
                    }else {
                        Toast.makeText(GalleryActivity.this,"No Designs Found!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void filterDesign() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = GalleryActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.price_category, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Select Sharing Category");
        RadioGroup radiogroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
        RadioButton radioWithPrice = (RadioButton) dialogView.findViewById(R.id.radioWithPrice);
        RadioButton radioWithoutPrice = (RadioButton) dialogView.findViewById(R.id.radioWithoutPrice);
        RadioButton radioWithSeperatePrice = (RadioButton) dialogView.findViewById(R.id.radioWithSeperatePrice);
        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioWithoutPrice) {
                    sp.setCategoryPref(0);
                } else if (checkedId == R.id.radioWithPrice) {
                    sp.setCategoryPref(1);
                } else if (checkedId == R.id.radioWithSeperatePrice) {
                    sp.setCategoryPref(2);
                }
            }
        });

        if (category == 0) {
            radioWithoutPrice.setChecked(true);
        } else if (category == 1) {
            radioWithPrice.setChecked(true);
        } else if (category == 2) {
            radioWithSeperatePrice.setChecked(true);
        }
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void syncJewelry() {
        //Sync
        final ProgressDialog progressDialog = new ProgressDialog(GalleryActivity.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest streq = new VolleyRequestHandler().GeneralVolleyRequest(
                getApplicationContext(),
                0,
                getJewelry,
                new VolleyRequestHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONArray userArray = new JSONArray(result);
                            if (userArray.length() > 0) {
                                for (int i = 0; i < userArray.length(); i++){
                                    JSONObject jewelry = userArray.getJSONObject(i);
                                    String name = jewelry.optString("name");
                                    String cost = jewelry.optString("cost");
                                    if (db.getProductExistorNot(name) == 1){
                                        db.updateCost(name,Integer.parseInt(cost));

                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        checkedImageCost.clear();
                        checkedImageUri.clear();
                        checkedImageName.clear();
                        adapterItems.clear();
                        jewelryList = db.getAllFace();
                        for (DesignModel f : jewelryList) {
                            final String path = f.getDesignUri();
                            File file = new File(path);
                            Uri uri = Uri.fromFile(file);
                            adapterItems.add(f);
                        }

                        for (int i = 0; i < adapterItems.size(); i++) {
                            adapterItems.get(i).setSelected(0);
                        }

                        rcvGallery.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 2));
                        galleryAdapter = new GalleryAdapter(GalleryActivity.this,adapterItems,GalleryActivity.this);
                        rcvGallery.setAdapter(galleryAdapter);
                        //counter = 0;
                        //labelUpdate();
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(String fail) {
                        VolleyLog.e("Tag", fail);
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }
        );

        streq.setRetryPolicy(new DefaultRetryPolicy(
                ApiConstants.TIMEOUT_MS,
                ApiConstants.DEFAULT_RETRY,
                ApiConstants.FLOAT_FRACTION
        ));

        Volley.newRequestQueue(getApplicationContext()).add(streq);
    }

    private void priceSearch() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = GalleryActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_price_search, null);
        final EditText et_fromprice = (EditText) dialogView.findViewById(R.id.et_fromprice);
        final EditText et_toprice = (EditText) dialogView.findViewById(R.id.et_toprice);
        Button btn_search = (Button) dialogView.findViewById(R.id.btn_search);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Filter with Price Range");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_fromprice.getText().toString())) {
                    Toast.makeText(GalleryActivity.this, "Enter From Price", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_toprice.getText().toString())) {
                    Toast.makeText(GalleryActivity.this, "Enter To Price", Toast.LENGTH_LONG).show();
                } else {
                    adapterItems.clear();
                    int priceFrom = Integer.parseInt(et_fromprice.getText().toString());
                    int priceTo = Integer.parseInt(et_toprice.getText().toString());
                    jewelryList = db.getDesignwithPrice(priceFrom, priceTo);
                    for (DesignModel f : jewelryList) {
                        final String path = f.getDesignUri();
                        File file = new File(path);
                        Uri uri = Uri.fromFile(file);
                        adapterItems.add(f);
                        galleryAdapter.notifyDataSetChanged();
                    }
                }
                alertDialog.dismiss();
            }
        });
    }

    private void searchJewelry() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GalleryActivity.this);
        LayoutInflater inflater = GalleryActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_search_product, null);
        final EditText etSearch = (EditText) dialogView.findViewById(R.id.etSearch);
        final Button btSearch = (Button) dialogView.findViewById(R.id.btSearch);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Filter by Design Name");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItems.clear();
                String strText = etSearch.getText().toString();
                if (!strText.equals("")) {
                    List<DesignModel> dm = db.findProduct(strText);
                    if (dm != null && dm.size() > 0) {
                        for (int i = 0; i < dm.size(); i++) {
                            adapterItems.add(dm.get(i));
                            galleryAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(GalleryActivity.this, "Please Enter an Existing Design Name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(GalleryActivity.this, "Please Enter a Design Name", Toast.LENGTH_LONG).show();
                }
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void openCamera(DesignModel product, AlertDialog chooserDialog) {
        updateDesign = product;
        this.chooserDialog = chooserDialog;
        if (isCameraAllowed() && isReadStorageAllowed()){
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File photo = null;
            try {
                photo = this.createTemporaryFile("picture", ".jpg");
                photo.delete();
            }
            catch(Exception e){}
            //mImageUri = Uri.fromFile(photo);
            mImageUri = FileProvider.getUriForFile(GalleryActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, 0);

        }else if (isCameraAllowed() && !isReadStorageAllowed()){
            requestStoragePermission();
        }else if (!isCameraAllowed() && isReadStorageAllowed()){
            requestCameraPermission();
        }else {
            requestStoragePermission();
            requestCameraPermission();
        }
    }

    @Override
    public void openBrowser(DesignModel product,AlertDialog chooserDialog) {
        updateDesign = product;
        this.chooserDialog = chooserDialog;
        if (isReadStorageAllowed()){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1);
        }else
            requestStoragePermission();
    }

    private File createTemporaryFile(String picture, String s) throws Exception{
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/Jewelry/");
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return File.createTempFile(picture, s, tempDir);
    }

    private boolean isReadStorageAllowed(){
        int result = ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
        }
        ActivityCompat.requestPermissions(GalleryActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    private boolean isCameraAllowed(){
        int result = ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void requestCameraPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,Manifest.permission.CAMERA)){
        }
        ActivityCompat.requestPermissions(GalleryActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isStoragePermission = true;
            }else{
                Toast.makeText(GalleryActivity.this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isCameraPermission = true;
            }else{
                Toast.makeText(GalleryActivity.this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("GalleryActivity","Activity Result");
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    /*UCrop.of(mImageUri, Uri.fromFile(new File(GalleryActivity.this.getCacheDir(),"CropImage.jpg")))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(1000, 1000)
                            .start(this, UCrop.REQUEST_CROP);*/

                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                        mServerUri = mImageUri;
                        SaveImage(bitmap);
                        if (chooserDialog != null && chooserDialog.isShowing())
                            chooserDialog.dismiss();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    Uri selectedImage = data.getData();
                    /*try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        mServerUri = selectedImage;

                        UCrop.of(mServerUri, Uri.fromFile(new File(this.getCacheDir(),"CropImage.jpg")))
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(1000, 1000)
                                .start( this, UCrop.REQUEST_CROP);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (OutOfMemoryError error){
                        Toast.makeText(this,"Image Size is too large.Please Reupload it freshly.",Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }*/
                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        mServerUri = selectedImage;
                        SaveImage(bitmap);
                        if (chooserDialog != null && chooserDialog.isShowing())
                            chooserDialog.dismiss();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case UCrop.REQUEST_CROP:
                final Uri mImageUri = UCrop.getOutput(data);
                getApplicationContext().getContentResolver().notifyChange(mImageUri, null);
                ContentResolver cr = this.getContentResolver();
                Bitmap bitmap;
                try
                {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
                    //img_preview.setImageBitmap(bitmap);
                    mServerUri = mImageUri;
                    SaveImage(bitmap);
                }
                catch (Exception e){}
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate){
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFileFromSDCard(String filename){
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() +"/"+ filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Jewelry");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            savedNewURI = file.toString();
            Log.e("New URI",savedNewURI);
            if (db != null){
                long row = db.updateURI(updateDesign.getDesignName(),savedNewURI);
                if (row > 0){
                    updateDesign.setDesignUri(savedNewURI);
                    galleryAdapter.notifyDataSetChanged();
                    //Todo
                    //uncomment
                    uploadNewImageToServer(updateDesign,savedNewURI);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadNewImageToServer(DesignModel updateDesign, String savedNewURI) {
        try{
            String filePath = getRealPathFromURIPath(mServerUri, this);
            File file = new File(filePath);
            Log.d("tAG", "Filename " + file.getName());
            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
            RequestBody mName = RequestBody.create(MediaType.parse("text/plain"), updateDesign.getDesignName());
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASEURL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
            Call<String> fileUpload = uploadImage.uploadFile(fileToUpload,mName);
            fileUpload.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("GalleryActivity","New File uploaded");
                    //Todo
                    //Need to update URL in main table

                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("tag", "Error " + t.getMessage());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Context activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
