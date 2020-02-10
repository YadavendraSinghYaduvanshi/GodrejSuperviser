package com.cpm.dailyentry;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.godrejsupervisor.R;
import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StoreImageActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageView img_cam, img_clicked;
    FloatingActionButton btn_save;
    String _pathforcheck, _path, str, searchSTORE_CODE, searchSTORE_NAME;
    String store_cd, visit_date, username, app_ver, store_name;
    private SharedPreferences preferences;
    AlertDialog alert;
    String img_str;
    private GODREJDatabase database;
    String lat = "0.0", lon = "0.0";
    GoogleApiClient mGoogleApiClient;
    ArrayList<CoverageBean> coverage_list;
    String datacheck = "";
    String[] words;
    String validity;
    private LocationManager locmanager = null;
    boolean enabled;
    Context context;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_image);
        uiDATA();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");
            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            database.open();
            if (database.getCoverageSpecificData(store_cd, visit_date).size() == 0) {
                database.deleteJaurneyPlanData(store_cd);
            }
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        database.open();
        if (database.getCoverageSpecificData(store_cd, visit_date).size() == 0) {
            database.deleteJaurneyPlanData(store_cd);
        }
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    private void uiDATA() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);
        btn_save = (FloatingActionButton) findViewById(R.id.btn_save_selfie);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        ////for store search
        searchSTORE_CODE = preferences.getString(CommonString.KEY_SEARCH_STORE_CODE, "");
        searchSTORE_NAME = preferences.getString(CommonString.KEY_SEARCH_STORE_NAME, "");
        setTitle("Store Image - " + visit_date);
        str = CommonString.FILE_PATH;
        database = new GODREJDatabase(context);
        database.open();
        coverage_list = database.getCoverageData(visit_date);
        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");
            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_cam_selfie:
                _pathforcheck = store_cd + "_STOREIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, _path);
                break;
            case R.id.btn_save_selfie:
                if (img_str != null) {
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.alert_save))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                try {
                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_cd);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(username);
                                                    cdata.setInTime(getCurrentTime());
                                                    cdata.setOutTime("");
                                                    cdata.setReason("");
                                                    cdata.setReasonid("0");
                                                    cdata.setLatitude(lat);
                                                    cdata.setLongitude(lon);
                                                    cdata.setImage(_pathforcheck);
                                                    cdata.setStatus(CommonString.KEY_CHECK_IN);
                                                    cdata.setRemark("");
                                                    cdata.setImage(img_str);
                                                    cdata.setImage02("");
                                                    cdata.setSearch_storeCODE(searchSTORE_CODE);
                                                    cdata.setSearch_storeNAME(searchSTORE_NAME);
                                                    new BackgroundTask(context, cdata).execute();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int id) {
                                        dialog.cancel();
                                    }
                                });
                        alert = builder.create();
                        alert.show();
                    } else {
                        Snackbar.make(btn_save, R.string.nonetwork, Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    Snackbar.make(btn_save, "Please click the store image.", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    try {
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Store Image", username);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                            img_cam.setImageBitmap(bmp);
                            img_clicked.setVisibility(View.GONE);
                            img_cam.setVisibility(View.VISIBLE);
                            //Set Clicked image to Imageview
                            img_str = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            lon = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void message(String msg) {
        Snackbar.make(btn_save, msg, Snackbar.LENGTH_LONG).show();
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private class BackgroundTask extends AsyncTask<Void, String, String> {
        private Context context;
        private CoverageBean cdata;

        BackgroundTask(Context context, CoverageBean coverageBean) {
            this.cdata = coverageBean;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Uploading Data", "Please wait...", false, false);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {

                String onXML = "[DATA][USER_DATA][STORE_CD]"
                        + cdata.getStoreId()
                        + "[/STORE_CD]" + "[VISIT_DATE]"
                        + cdata.getVisitDate()
                        + "[/VISIT_DATE][LATITUDE]"
                        + cdata.getLatitude()
                        + "[/LATITUDE][APP_VERSION]"
                        + app_ver
                        + "[/APP_VERSION][LONGITUDE]"
                        + cdata.getLongitude()
                        + "[/LONGITUDE][IN_TIME]"
                        + getCurrentTime()
                        + "[/IN_TIME][OUT_TIME]"
                        + "00:00:00"
                        + "[/OUT_TIME][UPLOAD_STATUS]"
                        + cdata.getStatus()
                        + "[/UPLOAD_STATUS][USER_ID]"
                        + cdata.getUserId()
                        + "[/USER_ID]" +
                        "[IMAGE_URL]"
                        + cdata.getImage()
                        + "[/IMAGE_URL]"
                        +
                        "[IMAGE_URL1]"
                        + ""
                        + "[/IMAGE_URL1]"
                        +
                        "[REASON_ID]"
                        + "0"
                        + "[/REASON_ID]" +
                        "[REASON_REMARK]"
                        + ""
                        + "[/REASON_REMARK][/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                request.addProperty("onXML", onXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);
                Object result = (Object) envelope.getResponse();
                datacheck = result.toString();
                datacheck = datacheck.replace("\"", "");
                words = datacheck.split("\\;");
                validity = (words[0]);
                if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    database.open();
                    database.InsertCoverageData(cdata);
                    database.updateStoreStatusOnLeave(cdata.getStoreId(), cdata.getVisitDate(), CommonString.KEY_CHECK_IN);

                    ////////////////PREFERENCESSSS VALUEEEEEEEEEEE
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                    editor.commit();
                    return CommonString.KEY_SUCCESS;
                } else {
                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                    }
                }
            } catch (MalformedURLException e) {
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });

            } catch (IOException e) {
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                loading.dismiss();
                Crashlytics.logException(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            loading.dismiss();
            if (result != null && result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                Intent in = new Intent(context, StoreEntry.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                StoreImageActivity.this.finish();
                Toast.makeText(context, "Store check in Successfully Uploaded.", Toast.LENGTH_LONG).show();
            } else if (!result.equals("")) {
                Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_LONG).show();
            }
        }
    }
}
