package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;


import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.StoreBean;

import com.cpm.capitalfoods.R;
import com.cpm.PnGSupervisor.MainMenuActivity;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AssetInsertdataGetterSetter;
import com.cpm.xmlGetterSetter.CallsGetterSetter;
import com.cpm.xmlGetterSetter.CompetitionPromotionGetterSetter;
import com.cpm.xmlGetterSetter.FacingCompetionCompanyGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.POIGetterSetter;
import com.cpm.xmlGetterSetter.PromotionInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

@SuppressWarnings("deprecation")
public class UploadDataActivity extends Activity {

    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    String app_ver;
    private String visit_date, username;
    private SharedPreferences preferences;
    private GSKDatabase database;
    private String reasonid, faceup, stock, length;
    private int factor, k;
    String datacheck = "";
    String[] words;
    String validity, storename;
    int mid;
    String sod = "";
    String total_sku = "";
    String sku = "";
    String sos_data = "";
    String category_data = "";
    Data data;

    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();

    private FailureGetterSetter failureGetterSetter = null;
    StoreBean storestatus = new StoreBean();
    static int counter = 1;
    private ArrayList<StoreBean> store_detail = new ArrayList<StoreBean>();

    /*private ArrayList<AttendenceBean> attendenceData = new ArrayList<AttendenceBean>();
    private ArrayList<GATEbEAN> gateBean = new ArrayList<GATEbEAN>();
    private ArrayList<DeepFreezerTypeGetterSetter> deepfreezerData = new ArrayList<DeepFreezerTypeGetterSetter>();
    private ArrayList<FacingCompetitorGetterSetter> facingCompetitorData = new ArrayList<FacingCompetitorGetterSetter>();
    */
    private ArrayList<AssetInsertdataGetterSetter> assetInsertdata = new ArrayList<AssetInsertdataGetterSetter>();
    private ArrayList<PromotionInsertDataGetterSetter> promotionData = new ArrayList<PromotionInsertDataGetterSetter>();
    //private ArrayList<FoodStoreInsertDataGetterSetter> foodStoredata = new ArrayList<FoodStoreInsertDataGetterSetter>();
    private ArrayList<StockNewGetterSetter> stockData = new ArrayList<StockNewGetterSetter>();
    private ArrayList<StockNewGetterSetter> stockImgData = new ArrayList<StockNewGetterSetter>();
    ArrayList<POIGetterSetter> poiData = new ArrayList<POIGetterSetter>();
    ArrayList<POIGetterSetter> competitionpoiData = new ArrayList<POIGetterSetter>();
    ArrayList<FacingCompetionCompanyGetterSetter> facingcompetition = new ArrayList<FacingCompetionCompanyGetterSetter>();
    ArrayList<CompetitionPromotionGetterSetter> promotioncompetitionData = new ArrayList<CompetitionPromotionGetterSetter>();

    private ArrayList<CallsGetterSetter> callsData = new ArrayList<CallsGetterSetter>();

    boolean upload_status;
    String result;
    String Path;
    boolean image_valid;

    String errormsg = "", status;
    boolean up_success_flag = true;

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
        database = new GSKDatabase(this);
        database.open();

        Intent i = getIntent();
        upload_status = i.getBooleanExtra("UploadAll", false);

        Path = Environment.getExternalStorageDirectory() + "/PNGsupervisor/";

        new UploadTask(this).execute();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        database.close();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
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
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                data = new Data();

				/*HttpParams myParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(myParams, 10000);
				HttpConnectionParams.setSoTimeout(myParams, 10000);
				HttpClient httpclient = new DefaultHttpClient();
				InputStream inputStream = null;*/

                if (upload_status == false)

                {
                    coverageBeanlist = database.getCoverageData(visit_date);

                } else {
                    coverageBeanlist = database.getCoverageData(null);

                }


                if (coverageBeanlist.size() > 0) {

                    if (coverageBeanlist.size() == 1) {
                        factor = 50;
                    } else {

                        factor = 100 / (coverageBeanlist.size());
                    }
                }

                for (int i = 0; i < coverageBeanlist.size(); i++) {

					/* storestatus = database.getStoreStatus(coverageBeanlist.get(
							 i).getStoreId());
					 */
                    //					if (upload_status) {
                    //						storestatus.setCheckout_status("C");
                    //					}

                    //					if ((storestatus.getCheckout_status().equalsIgnoreCase(
                    //
                    //					CommonString.KEY_L) || storestatus.getCheckout_status()
                    //							.equalsIgnoreCase(
                    //
                    //							CommonString.KEY_C))) {

                    // if (true) {

                    //	if (!coverageBeanlist.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D)) {


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


                            +
                            "[IMAGE_URL1]"
                            + coverageBeanlist.get(i).getImage02()
                            + "[/IMAGE_URL1]"


                            +

                            "[REASON_ID]"
                            + coverageBeanlist.get(i).getReasonid()
                            + "[/REASON_ID]" +
                            "[REASON_REMARK]"
                            + coverageBeanlist.get(i).getRemark()
                            + "[/REASON_REMARK][/USER_DATA][/DATA]";


                    SoapObject request = new SoapObject(
                            CommonString.NAMESPACE,
                            CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE);
                    request.addProperty("onXML", onXML);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(
                            CommonString.URL);

                    androidHttpTransport.call(
                            CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE,
                            envelope);
                    Object result = (Object) envelope.getResponse();

                    datacheck = result.toString();
                    datacheck = datacheck.replace("\"", "");
                    words = datacheck.split("\\;");
                    validity = (words[0]);

                    if (validity
                            .equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        database.updateCoverageStatus(coverageBeanlist
                                .get(i).getMID(), CommonString.KEY_P);

                        database.updateStoreStatusOnLeave(
                                coverageBeanlist.get(i).getStoreId(),
                                coverageBeanlist.get(i).getVisitDate(),
                                CommonString.KEY_P);
                    } else {
                        if (result.toString().equalsIgnoreCase(
                                CommonString.KEY_FALSE)) {
                            return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                        }
                        if (result.toString().equalsIgnoreCase(
                                CommonString.KEY_FAILURE)) {
                            return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                        }

                    }

                    mid = Integer.parseInt((words[1]));

                    if (coverageBeanlist.get(i).getImage() != null
                            && !coverageBeanlist.get(i).getImage().equals("")) {

                        if (new File(
                                Environment.getExternalStorageDirectory() + "/PNGsupervisor/"

                                        + coverageBeanlist.get(i).getImage())
                                .exists()) {

                            result = UploadAssetImage(coverageBeanlist.get(i).getImage());

                            if (result
                                    .toString()
                                    .equalsIgnoreCase(
                                            CommonString.KEY_FALSE)) {

                                return "StoreImages";
                            } else if (result.equals(CommonString.KEY_FAILURE)) {

                                return "StoreImages"
                                        + "," + errormsg;
                            }

                            runOnUiThread(new Runnable() {

                                public void run() {


                                    message.setText("Images Uploaded");
                                }
                            });


                        }
                    }

                    if (coverageBeanlist.get(i).getImage02() != null
                            && !coverageBeanlist.get(i).getImage02().equals("")) {

                        if (new File(
                                Environment.getExternalStorageDirectory() + "/PNGsupervisor/"

                                        + coverageBeanlist.get(i).getImage02())
                                .exists()) {

                            result = UploadAssetImage(coverageBeanlist.get(i).getImage02());

                            if (result
                                    .toString()
                                    .equalsIgnoreCase(
                                            CommonString.KEY_FALSE)) {

                                return "StoreImages";
                            } else if (result.equals(CommonString.KEY_FAILURE)) {

                                return "StoreImages"
                                        + "," + errormsg;
                            }

                            runOnUiThread(new Runnable() {

                                public void run() {

                                    message.setText("Images Uploaded");
                                }
                            });

                        }
                    }

                    data.value = 30;
                    data.name = "Uploading";

                    publishProgress(data);

                    String final_xml = "";


                    database.open();

                    database.updateCoverageStatus(coverageBeanlist.get(i)
                            .getMID(), CommonString.KEY_D);
                    database.updateStoreStatusOnLeave(coverageBeanlist.get(i)
                            .getStoreId(), coverageBeanlist.get(i)
                            .getVisitDate(), CommonString.KEY_D);

                    data.value = factor * (i + 1);
                    data.name = "Uploading";

                    publishProgress(data);


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

                    final String sos_xml = "[DATA]" + final_xml
                            + "[/DATA]";

                    request = new SoapObject(
                            CommonString.NAMESPACE,
                            CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS);
                    request.addProperty("onXML", sos_xml);
					/*request.addProperty("KEYS", "COVERAGE_STATUS");
					request.addProperty("USERNAME", username);
					request.addProperty("MID", mid);*/

                    envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    androidHttpTransport = new HttpTransportSE(
                            CommonString.URL);

                    androidHttpTransport.call(
                            CommonString.SOAP_ACTION + CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS,
                            envelope);
                    result = (Object) envelope.getResponse();


                    if (result.toString().equalsIgnoreCase(
                            CommonString.KEY_NO_DATA)) {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }

                    if (result.toString().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.MEHTOD_UPLOAD_COVERAGE_STATUS;
                    }


                }


            } catch (MalformedURLException e) {

                up_success_flag = false;

                final AlertMessage message = new AlertMessage(
                        UploadDataActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {

                up_success_flag = false;

                return "Fail";

				/*final AlertMessage message = new AlertMessage(
						UploadDataActivity.this,
						AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						message.showMessage();

					}
				});*/
            } catch (Exception e) {

                up_success_flag = false;

                final AlertMessage message = new AlertMessage(
                        UploadDataActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            if (up_success_flag == true) {

                database.deleteAllTables();

                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_FAILURE;
            }


        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub

            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {

                if (upload_status == true) {

					/*Intent intent = new Intent(getBaseContext(),
							UploadImageActivity.class);
					intent.putExtra("UploadAll", true);
					startActivity(intent);*/

                    Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    AlertMessage message = new AlertMessage(
                            UploadDataActivity.this,
                            AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                    message.showMessage();

                    //database.deleteAllTables();


                }
            } else if (result.equalsIgnoreCase("Fail")) {


                //Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();

                AlertMessage message = new AlertMessage(
                        UploadDataActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "success", null);
                message.showMessage();

            } else if (result.equals(CommonString.KEY_FAILURE) || !result.equals("")) {

                AlertMessage message = new AlertMessage(
                        UploadDataActivity.this, CommonString.ERROR + result, "success", null);
                message.showMessage();
            }


        }
    }

    class Data {
        int value;
        String name;
    }


    String makeJson(String json) {
        json = json.replace("\\", "");
        json = json.replace("\"[", "[");
        json = json.replace("]\"", "]");

        return json;
    }


    public JSONArray makeJsonArray(JSONArray json) {
        JSONArray jason = new JSONArray();

        for (int i = 0; i < json.length(); i++) {


        }
        return json;
    }

    public String UploadImage(String path, String store_cd) throws Exception {

        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(
                Path + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", "StoreImages");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString.URL);

        androidHttpTransport
                .call(CommonString.SOAP_ACTION_UPLOAD_IMAGE,
                        envelope);
        Object result = (Object) envelope.getResponse();

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

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(Path + path).delete();
            SharedPreferences.Editor editor = preferences
                    .edit();
            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
            editor.commit();
        }

        return "";
    }


    public String UploadAssetImage(String path) throws Exception {

        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(
                Path + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", "StoreImages");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString.URL);

        androidHttpTransport
                .call(CommonString.SOAP_ACTION_UPLOAD_IMAGE,
                        envelope);
        Object result = (Object) envelope.getResponse();

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

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }
        } else {
            new File(Path + path).delete();
        }

        return "";
    }
}
