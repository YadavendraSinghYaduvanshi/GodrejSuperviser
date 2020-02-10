package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;


import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;

import com.cpm.godrejsupervisor.R;
import com.cpm.GodrejSupervisor.MainMenuActivity;
import com.cpm.xmlGetterSetter.AuditQuestion;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

@SuppressWarnings("deprecation")
public class UploadDataActivity extends Activity {
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    private FailureGetterSetter failureGetterSetter = null;
    ArrayList<AuditQuestion> storeAuditDataList = new ArrayList<>();
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    String app_ver;
    private String visit_date, username;
    private SharedPreferences preferences;
    private GODREJDatabase database;
    private int factor;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    Data data;
    boolean upload_status;
    String errormsg = "", resultFinal, exceptionMessage = "";
    boolean up_success_flag = true, isError = false;
    String dialogvalue = "Uploading Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new GODREJDatabase(this);
        database.open();
        new UploadTask(this).execute();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        UploadDataActivity.this.finish();
    }

    private class UploadTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                data = new Data();
                data.value = 10;
                data.name = "Uploading Data";
                data.dialogname = dialogvalue;
                publishProgress(data);
                if (upload_status == false) {
                    database.open();
                    coverageBeanlist = database.getCoverageData(visit_date);
                } else {
                    database.open();
                    coverageBeanlist = database.getCoverageData(null);
                }

                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    if (!coverageBeanlist.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        String onXML = "[DATA][USER_DATA][STORE_CD]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][LATITUDE]"
                                + coverageBeanlist.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + coverageBeanlist.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + coverageBeanlist.get(i).getInTime()
                                + "[/IN_TIME][OUT_TIME]"
                                + coverageBeanlist.get(i).getOutTime()
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]"
                                + username
                                + "[/USER_ID]" +
                                "[IMAGE_URL]"
                                + coverageBeanlist.get(i).getImage()
                                + "[/IMAGE_URL]"
                                + "[IMAGE_URL1]"
                                + coverageBeanlist.get(i).getImage02()
                                + "[/IMAGE_URL1]"
                                + "[REASON_ID]"
                                + coverageBeanlist.get(i).getReasonid()
                                + "[/REASON_ID]" +
                                "[REASON_REMARK]"
                                + coverageBeanlist.get(i).getRemark()
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
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_P);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(),
                                    CommonString.KEY_P);
                            coverageBeanlist.get(i).setStatus(CommonString.KEY_P);
                        } else {
                            isError = true;
                        }
                        mid = Integer.parseInt((words[1]));
                        data.value = 30;
                        data.name = "Uploading Coverage Data";
                        data.dialogname = dialogvalue;
                        publishProgress(data);


                        // StoreAudit data
                        String final_xml = "";
                        onXML = "";
                        database.open();
                        storeAuditDataList = database.getStoreAuditCompleteData(coverageBeanlist.get(i).getStoreId());
                        if (storeAuditDataList.size() > 0) {

                            for (int j = 0; j < storeAuditDataList.size(); j++) {
                                onXML = "[STORE_AUDIT_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[QUESTION_CATEGORY_ID]"
                                        + storeAuditDataList.get(j).getQUESTION_CATEGORY_ID().get(0)
                                        + "[/QUESTION_CATEGORY_ID]"
                                        + "[QUESTION_CD]"
                                        + storeAuditDataList.get(j).getQUESTION_ID().get(0)
                                        + "[/QUESTION_CD]"
                                        + "[CAMERA_ALLOW]"
                                        + storeAuditDataList.get(j).getCAMERA_ALLOW().get(0)
                                        + "[/CAMERA_ALLOW]"
                                        + "[AUDIT_IMG]"
                                        + storeAuditDataList.get(j).getAuditQuestion_cam()
                                        + "[/AUDIT_IMG]"
                                        + "[REMARK_ALLOW]"
                                        + storeAuditDataList.get(j).getREMARK_ALLOW().get(0)
                                        + "[/REMARK_ALLOW]"
                                        + "[AUDIT_REMARK]"
                                        + storeAuditDataList.get(j).getAudit_remark()
                                        + "[/AUDIT_REMARK]"
                                        + "[CURRECT_ANSWER_CD]"
                                        + storeAuditDataList.get(j).getCurrectanswer_cd()
                                        + "[/CURRECT_ANSWER_CD]"
                                        + "[/STORE_AUDIT_DATA]";

                                final_xml = final_xml + onXML;
                            }
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "STORE_AUDIT_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                isError = true;
                            }

                            data.value = 60;
                            data.name = "STORE_AUDIT_DATA";
                            data.dialogname = dialogvalue;
                            publishProgress(data);
                        }

                        final_xml = "";
                        // SET COVERAGE STATUS
                        final_xml = "";
                        onXML = "";
                        onXML = "[COVERAGE_STATUS][STORE_ID]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_ID]"
                                + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE]"
                                + "[USER_ID]"
                                + coverageBeanlist.get(i).getUserId()
                                + "[/USER_ID]"
                                + "[STATUS]"
                                + CommonString.KEY_D
                                + "[/STATUS]"
                                + "[/COVERAGE_STATUS]";

                        final_xml = final_xml + onXML;
                        final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                        request = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                        request.addProperty("onXML", sos_xml);
                        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope);
                        result = (Object) envelope.getResponse();
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            database.open();
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString.KEY_D);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(),
                                    CommonString.KEY_D);
                            coverageBeanlist.get(i).setStatus(CommonString.KEY_D);
                        } else {
                            isError = true;
                        }
                        data.value = 90;
                        data.name = "Updating Coverage Status";
                        data.dialogname = dialogvalue;
                        publishProgress(data);
                    }
                }
                File dir = new File(CommonString.FILE_PATH);
                ArrayList<String> list = new ArrayList();
                list = getFileNames(dir.listFiles());
                Object result;
                if (list.size() > 0) {
                    //  dialog.setTitle();
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        if (list.get(i1).contains("_STOREIMG_") || list.get(i1).contains("_NONWORKING_") ||
                                list.get(i1).contains("_STORE_CHECKOUTIMG_")) {
                            if (new File(CommonString.FILE_PATH + list.get(i1)).exists()) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = UploadImage(originalFile.getName(), "StoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    isError = true;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        message.setText("StoreImages Uploaded");
                                    }
                                });
                                data.value = 50;
                                data.name = "StoreImages";
                                data.dialogname = "Uploading images";
                                publishProgress(data);
                            }


                        } else if (list.get(i1).contains("_AUDITIMG_")) {
                            if (new File(CommonString.FILE_PATH + list.get(i1)).exists()) {
                                File originalFile = new File(CommonString.FILE_PATH + list.get(i1));
                                result = UploadImage(originalFile.getName(), "AuditImages");
                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                    isError = true;
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        message.setText("AuditImages Uploaded");
                                    }
                                });
                                data.value = 80;
                                data.name = "AuditImages";
                                data.dialogname = "Uploading images";
                                publishProgress(data);
                            }
                        }

                    }
                    // SET COVERAGE STATUS
                    if (coverageBeanlist.size() > 0) {
                        if (!isError)
                            for (int k = 0; k < coverageBeanlist.size(); k++) {
                                if (coverageBeanlist.get(k).getStatus().equals(CommonString.KEY_D)) {
                                    String final_xml1 = "", onXML1 = "";
                                    onXML1 = "[COVERAGE_STATUS][STORE_ID]"
                                            + coverageBeanlist.get(k).getStoreId()
                                            + "[/STORE_ID]"
                                            + "[VISIT_DATE]"
                                            + coverageBeanlist.get(k).getVisitDate()
                                            + "[/VISIT_DATE]"
                                            + "[USER_ID]"
                                            + coverageBeanlist.get(k).getUserId()
                                            + "[/USER_ID]"
                                            + "[STATUS]"
                                            + CommonString.KEY_U
                                            + "[/STATUS]"
                                            + "[/COVERAGE_STATUS]";

                                    final_xml1 = final_xml1 + onXML1;
                                    final String sos_xml = "[DATA]" + final_xml1 + "[/DATA]";
                                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                                    request.addProperty("onXML", sos_xml);
                                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        database.open();
                                        database.updateCoverageStatus(coverageBeanlist.get(k).getMID(),
                                                CommonString.KEY_U);
                                        database.updateStoreStatusOnLeave(coverageBeanlist.get(k).getStoreId(),
                                                coverageBeanlist.get(k).getVisitDate(),
                                                CommonString.KEY_U);
                                        coverageBeanlist.get(k).setStatus(CommonString.KEY_U);
                                        database.deleteSpecificStoreData(coverageBeanlist.get(k).getStoreId());
                                    }
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        isError = true;
                                    }

                                    data.value = 100;
                                    data.name = "Updating status..";
                                    data.dialogname = "Updating status";
                                    publishProgress(data);
                                    resultFinal = result.toString();
                                }
                            }
                    }
                }

            } catch (MalformedURLException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();

            } catch (IOException e) {
                up_success_flag = false;
                exceptionMessage = e.toString();

            } catch (Exception e) {
                up_success_flag = false;
                exceptionMessage = e.toString();
            }
            if (up_success_flag) {
                return resultFinal;
            } else {
                return exceptionMessage;
            }
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
            tv_title.setText(values[0].dialogname);


        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (isError) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadDataActivity.this);
                builder.setTitle("Parinaam");
                builder.setMessage("Uploaded successfully with some problem . Please try again").setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //temporary code
                                Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                if (result != null && result.equals(CommonString.KEY_SUCCESS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadDataActivity.this);
                    builder.setTitle(getString(R.string.parinaam));
                    builder.setMessage(CommonString.MESSAGE_UPLOAD_DATA).setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getBaseContext(), MainMenuActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else if (result != null && result.equals(CommonString.KEY_FAILURE) || result != null && !result.equals("")) {
                    message(CommonString.ERROR + result);
                } else {
                    message("Data uploading error .Please try again ");
                }
            }
        }
    }

    private void message(String str) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Parinaam").setMessage(str);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(UploadDataActivity.this, MainMenuActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }


    class Data {
        int value;
        String name;
        String dialogname;
    }


    public String UploadImage(String path, String folder_path) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(CommonString.FILE_PATH + path, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2.1;
            height_tmp /= 2.1;
            scale *= 2.1;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(CommonString.FILE_PATH + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_IMAGE);
        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_path);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
        androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_IMAGE, envelope);

        Object result = envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                return CommonString.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();
            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);
            failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
            if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(CommonString.FILE_PATH + path).delete();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
            editor.commit();
        }
        return result.toString();
    }


    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

}
