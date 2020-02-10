package com.cpm.dailyentry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GODREJDatabase;
import com.cpm.godrejsupervisor.R;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.upload.UploadDataActivity;
import com.crashlytics.android.Crashlytics;

public class CheckOutStoreActivity extends AppCompatActivity implements View.OnClickListener {
    String username, visit_date, store_cd, app_ver, _pathforcheck, _path, store_name;
    ArrayList<CoverageBean> specific_coverageDATA = new ArrayList<>();
    private SharedPreferences preferences;
    ImageView img_cam, img_clicked;
    private GODREJDatabase database;
    ProgressDialog loading;
    Context context;
    AlertDialog alert;
    FloatingActionButton btn_save;
    String img_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
        uiDATA();
    }

    private void uiDATA() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);
        btn_save = (FloatingActionButton) findViewById(R.id.btn_save_selfie);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        ////for store search
        getSupportActionBar().setTitle("Checkout Image -" + visit_date);

        database = new GODREJDatabase(context);
        database.open();
        store_cd = getIntent().getStringExtra(CommonString.KEY_STORE_CD);
        specific_coverageDATA = database.getCoverageDataReason(visit_date, store_cd);
        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        // Create an instance of GoogleAPIClient.
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        database.open();

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_cam_selfie:
                _pathforcheck = store_cd + "_STORE_CHECKOUTIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, _path);
                break;
            case R.id.btn_save_selfie:
                if (img_str != null) {
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.alert_checkout))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                try {
                                                    new BackgroundTask(CheckOutStoreActivity.this, specific_coverageDATA).execute();
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
                    Snackbar.make(btn_save, "Please Click The Checkout Image.", Snackbar.LENGTH_SHORT).show();
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
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Store Checkout Image", username);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                            img_cam.setImageBitmap(bmp);
                            img_clicked.setVisibility(View.GONE);
                            img_cam.setVisibility(View.VISIBLE);
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

    private class BackgroundTask extends AsyncTask<Void, String, String> {
        private Context context;
        ArrayList<CoverageBean> specific_coverageDATA;

        BackgroundTask(Context context, ArrayList<CoverageBean> specific_coverageDATA) {
            this.context = context;
            this.specific_coverageDATA = specific_coverageDATA;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Uploading Checkout Data", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                if (specific_coverageDATA.size() > 0) {
                    String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
                            + username
                            + "[/USER_ID]" + "[STORE_ID]"
                            + store_cd
                            + "[/STORE_ID][LATITUDE]"
                            + specific_coverageDATA.get(0).getLatitude()
                            + "[/LATITUDE][LOGITUDE]"
                            + specific_coverageDATA.get(0).getLongitude()
                            + "[/LOGITUDE][CHECKOUT_DATE]"
                            + visit_date
                            + "[/CHECKOUT_DATE][CHECK_OUTTIME]"
                            + getCurrentTime()
                            + "[/CHECK_OUTTIME][CHECK_INTIME]"
                            + specific_coverageDATA.get(0).getInTime()
                            + "[/CHECK_INTIME][CREATED_BY]"
                            + username
                            + "[/CREATED_BY][/STORE_CHECK_OUT_STATUS]";
                    final String sos_xml = "[DATA]" + onXML + "[/DATA]";
                    SoapObject request = new SoapObject(CommonString.NAMESPACE, "Upload_Store_ChecOut_Status");
                    request.addProperty("onXML", sos_xml);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION + "Upload_Store_ChecOut_Status", envelope);
                    Object result = (Object) envelope.getResponse();
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                        return "Upload_Store_ChecOut_Status";
                    }
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return "Upload_Store_ChecOut_Status";
                    }

                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS_chkout)) {
                        database.open();
                        CoverageBean coverageBean = new CoverageBean();
                        coverageBean.setVisitDate(specific_coverageDATA.get(0).getVisitDate());
                        coverageBean.setOutTime(getCurrentTime());
                        coverageBean.setImage02(img_str);
                        coverageBean.setStatus(CommonString.KEY_C);
                        coverageBean.setStoreId(specific_coverageDATA.get(0).getStoreId());
                        database.updateOutTime(coverageBean);
                        database.open();
                        database.updateCoverageStatusOnCheckout(specific_coverageDATA.get(0).getStoreId()
                                , specific_coverageDATA.get(0).getVisitDate(), CommonString.KEY_C);
                        database.updateStoreStatusOnCheckout(specific_coverageDATA.get(0).getStoreId()
                                , specific_coverageDATA.get(0).getVisitDate(), CommonString.KEY_C);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STOREVISITED, "");
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.commit();
                        return CommonString.KEY_SUCCESS;
                    } else {
                        return "Upload_Store_ChecOut_Status";
                    }

                }
            } catch (MalformedURLException e) {
                loading.dismiss();
                final AlertMessage message = new AlertMessage(CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "socket_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            } catch (IOException e) {
                loading.dismiss();
                final AlertMessage message = new AlertMessage(CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                loading.dismiss();
                final AlertMessage message = new AlertMessage(CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "socket_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
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
            if (result != null && result.equals(CommonString.KEY_SUCCESS)) {
                Toast.makeText(context, "Checkout Successfully Uploaded.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, UploadDataActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                CheckOutStoreActivity.this.finish();
            } else {
                AlertMessage message = new AlertMessage((Activity) context, CommonString.ERROR + result, "socket_login", null);
                message.showMessage();
            }

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        CheckOutStoreActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            CheckOutStoreActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
