package com.cpm.dailyentry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;

import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.godrejsupervisor.R;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.crashlytics.android.Crashlytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class NonWorkingReason extends AppCompatActivity implements OnItemSelectedListener, OnClickListener {
    protected String _path, str, searchSTORE_CODE, searchSTORE_NAME, _UserId, visit_date, store_id, image1 = "",
            _pathforcheck = "", reasonname, reasonid, entry_allow, app_ver, intime, store_name;
    ArrayList<NonWorkingReasonGetterSetter> reasondata = new ArrayList<>();
    private ArrayAdapter<CharSequence> reason_adapter;
    private SharedPreferences preferences;
    ArrayList<JourneyPlanGetterSetter> jcp;
    RelativeLayout reason_lay, rel_cam;
    protected boolean status = true;
    private Spinner reasonspinner;
    private GODREJDatabase database;
    ProgressDialog loading;
    boolean netcheck = true;
    String datacheck = "";
    ImageButton camera;
    AlertDialog alert;
    String[] words;
    String validity;
    Context context;
    EditText text;
    FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonworking);
        reasonspinner = (Spinner) findViewById(R.id.spinner2);
        camera = (ImageButton) findViewById(R.id.imgcam);
        save = (FloatingActionButton) findViewById(R.id.save);
        text = (EditText) findViewById(R.id.reasontxt);
        reason_lay = (RelativeLayout) findViewById(R.id.layout_reason);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_CD, "");
        searchSTORE_CODE = preferences.getString(CommonString.KEY_SEARCH_STORE_CODE, "");
        searchSTORE_NAME = preferences.getString(CommonString.KEY_SEARCH_STORE_NAME, "");
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");

        setTitle("Non Working -" + visit_date);

        database = new GODREJDatabase(context);
        database.open();
        str = CommonString.FILE_PATH;
        intime = getCurrentTime();
        camera.setOnClickListener(this);
        save.setOnClickListener(this);
        jcp = database.getJCPData(visit_date);
        if (jcp.size() > 0) {
            try {
                for (int i = 0; i < jcp.size(); i++) {
                    boolean flag = false;
                    if (jcp.get(i).getUploadStatus().get(0).equals(CommonString.KEY_U) ||
                            jcp.get(i).getUploadStatus().get(0).equals(CommonString.KEY_D)
                            || jcp.get(i).getUploadStatus().get(0).equals(CommonString.KEY_C)
                            || jcp.get(i).getUploadStatus().get(0).equals(CommonString.STORE_STATUS_LEAVE)) {
                        flag = true;
                        reasondata.clear();
                        reasondata = database.getNonWorkingDataByFlag(flag);
                        break;
                    } else {
                        reasondata = database.getNonWorkingDataByFlag(flag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        reason_adapter = new ArrayAdapter<>(context, R.layout.spinner_custom_item);
        reason_adapter.add("-Select Reason-");
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason().get(0));
        }
        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(R.layout.spinner_custom_item);
        reasonspinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        database.open();
        if (database.getCoverageSpecificData(store_id, visit_date).size() == 0) {
            database.deleteJaurneyPlanData(store_id);
        }
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        NonWorkingReason.this.finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            database.open();
            if (database.getCoverageSpecificData(store_id, visit_date).size() == 0) {
                database.deleteJaurneyPlanData(store_id);
            }
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.spinner2:
                if (position != 0) {
                    reasonname = reasondata.get(position - 1).getReason().get(0);
                    reasonid = reasondata.get(position - 1).getReason_cd().get(0);
                    entry_allow = reasondata.get(position - 1).getEntry_allow().get(0);
                    if (entry_allow.equals("1")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        reason_lay.setVisibility(View.VISIBLE);
                    } else {

                        rel_cam.setVisibility(View.GONE);
                        reason_lay.setVisibility(View.VISIBLE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                    entry_allow = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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
                        if (new File(str + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, store_id, "Nonworking Image", _UserId);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, visit_date);
                            camera.setBackgroundResource((R.drawable.ic_menu_camera_done));
                            image1 = _pathforcheck;
                            _pathforcheck = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
                break;
        }
    }

    public boolean imageAllowed() {
        boolean result = true;
        if (entry_allow.equals("1")) {
            if (image1.equals("")) {
                result = false;
            }
        }

        return result;

    }

    public boolean textAllowed() {
        boolean result = true;
        if (text.getText().toString().trim().equals("")) {
            result = false;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.imgcam) {
            _pathforcheck = store_id + "_NONWORKING_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
            CommonFunctions.startAnncaCameraActivity(context, _path);
        }

        if (v.getId() == R.id.save) {
            if (validatedata()) {
                if (imageAllowed()) {
                    if (textAllowed()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(getString(R.string.parinaam));
                        builder.setMessage(getString(R.string.alert_save)).setCancelable(false)
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                try {
                                                    if (entry_allow.equals("0")) {
                                                        database.deleteAllTables();
                                                        jcp = database.getJCPData(visit_date);
                                                        for (int i = 0; i < jcp.size(); i++) {
                                                            String stoteid = jcp.get(i).getStore_cd().get(0);
                                                            CoverageBean cdata = new CoverageBean();
                                                            cdata.setStoreId(stoteid);
                                                            cdata.setVisitDate(visit_date);
                                                            cdata.setUserId(_UserId);
                                                            cdata.setInTime(intime);
                                                            cdata.setOutTime(getCurrentTime());
                                                            cdata.setReason(reasonname);
                                                            cdata.setReasonid(reasonid);
                                                            cdata.setLatitude("0.0");
                                                            cdata.setLongitude("0.0");
                                                            cdata.setImage(image1);
                                                            cdata.setImage02(image1);
                                                            cdata.setRemark(text.getText().toString().
                                                                    replaceAll("[&!@#%*()/^<>{}'$]", " "));
                                                            cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                            cdata.setSearch_storeCODE(searchSTORE_CODE);
                                                            cdata.setSearch_storeNAME(searchSTORE_NAME);
                                                            database.InsertCoverageData(cdata);
                                                            database.updateStoreStatusOnLeave(stoteid, visit_date, CommonString.STORE_STATUS_LEAVE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString(CommonString.KEY_STOREVISITED_STATUS + stoteid, "No");
                                                            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                            editor.commit();

                                                        }
                                                        NonWorkingReason.this.finish();
                                                    } else {
                                                        CoverageBean cdata = new CoverageBean();
                                                        cdata.setStoreId(store_id);
                                                        cdata.setVisitDate(visit_date);
                                                        cdata.setUserId(_UserId);
                                                        cdata.setInTime(intime);
                                                        cdata.setOutTime(getCurrentTime());
                                                        cdata.setReason(reasonname);
                                                        cdata.setReasonid(reasonid);
                                                        cdata.setLatitude("0.0");
                                                        cdata.setLongitude("0.0");
                                                        cdata.setImage(image1);
                                                        cdata.setImage02(image1);
                                                        cdata.setSearch_storeCODE(searchSTORE_CODE);
                                                        cdata.setSearch_storeNAME(searchSTORE_NAME);
                                                        cdata.setRemark(text.getText().toString().replaceAll("[&!@#%*()/^<>{}'$]", " "));
                                                        cdata.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                        new BackgroundTask(NonWorkingReason.this, cdata).execute();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
                                            }
                                        });
                        alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter required remark reason.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Capture Image.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Select a Reason.", Toast.LENGTH_SHORT).show();

            }
        }

    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equals("")) {
            result = true;
        }
        return result;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

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
            loading = ProgressDialog.show(context, "Uploading Nonworking Data", "Please wait...", false, false);
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
                        + getCurrentTime()
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
                        + cdata.getImage()
                        + "[/IMAGE_URL1]"
                        +
                        "[REASON_ID]"
                        + cdata.getReasonid()
                        + "[/REASON_ID]" +
                        "[REASON_REMARK]"
                        + cdata.getRemark()
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
                    database.updateStoreStatusOnLeave(cdata.getStoreId(), cdata.getVisitDate(), cdata.getStatus());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                    editor.commit();
                    return CommonString.KEY_SUCCESS;

                } else {
                    return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                }
            } catch (MalformedURLException e) {
                netcheck = false;
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });

            } catch (IOException e) {
                netcheck = false;
                loading.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                netcheck = false;
                loading.dismiss();
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
            if (netcheck) {
                if (result != null && result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    NonWorkingReason.this.finish();
                    Toast.makeText(context, "Nonworking Successfully Uploaded.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void message(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.parinaam));
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
