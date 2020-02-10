package com.cpm.dailyentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.GetterSetter.VisitorDetailGetterSetter;
import com.cpm.GetterSetter.VisitorSearchGetterSetter;
import com.cpm.database.GODREJDatabase;
 import com.cpm.godrejsupervisor.R;
import com.cpm.retrofit.PostApi;
import com.cpm.retrofit.StringConverterFactory;
import com.cpm.retrofit.UploadImageWithRetrofit;
import com.cpm.xmlHandler.XMLHandlers;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Retrofit;

import static com.cpm.compressimage.Utilities.saveBitmapToFile;


public class VisitorLoginActivity extends AppCompatActivity implements View.OnClickListener {
    String global_key_id = "0";
    Dialog dialog1;
    private LinearLayout lay_feedback;
    private Button feedback, save;
    private EditText ed_feedback;
    private RecyclerView lv_feedback;
    private GODREJDatabase database;
    LinearLayout heading;
    FloatingActionButton fab_save;
    RecyclerView recyclerView;
    VisitorDetailGetterSetter visitorLoginGetterSetter;
    ArrayList<VisitorDetailGetterSetter> visitorLoginData = new ArrayList<>();
    TextView tv_in_time, tv_out_time;
    String empid, emp_name, name, designation;
    boolean isUpdate = false;
    String intime_img, outtime_img;
    TextView tvname, tvdesignation;
    String error_msg = "";
    protected static String _pathforcheck = "";
    protected String _path, str;
    boolean ResultFlag = true;
    boolean camin_clicked = false, camout_clicked = false;
    Activity activity;
    String visit_date, username;
    RelativeLayout rel_intime, rel_outtime;
    ImageView img_intime, img_outtime;
    SharedPreferences preferences;
    String Path;
    ImageView imgcam_in, imgcam_out;
    Context context;
    Button btnclear, btngo;
    int eventType;
    VisitorSearchGetterSetter visitordata;
    EditText et_emp_name;
    MyRecyclerAdapter adapter;
    ArrayList<VisitorDetailGetterSetter> visitorListData;
    Dialog dialog_list;
    ArrayList<VisitorDetailGetterSetter> list;
    TextView tv_user_id;
    RadioButton cpm, loreal;
    RadioGroup radiogroup;
    LinearLayout lr_cpm, lr_loreal, linear_empl;
    String check = "CPM";
    EditText ed_name, ed_designation;
    Button btncleardata, loreal_clear;
    ImageView img_intime_loreal;
    ArrayList<VisitorDetailGetterSetter> feedback_ques = new ArrayList<>();
    RouteAdapter feedback_question;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        declaration();


    }

    void prepareList() {

        list = database.getVisitorData();

        //------------for state Master List---------------
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        for (int i = 0; i < list.size(); i++) {
            arrayAdapter.add(list.get(i).getName());
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //visitorSpn.setAdapter(arrayAdapter);
        //------------------------------------------------

    }

    void declaration() {
        activity = this;
        context = this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        btngo = (Button) findViewById(R.id.btngo);
        btnclear = (Button) findViewById(R.id.btnClear);
        et_emp_name = (EditText) findViewById(R.id.et_emp_name);
        tvname = (TextView) findViewById(R.id.tv_name);
        tvdesignation = (TextView) findViewById(R.id.tv_designation);
        imgcam_in = (ImageView) findViewById(R.id.imgcam_intime);
        imgcam_out = (ImageView) findViewById(R.id.imgcam_outtime);
        tv_in_time = (TextView) findViewById(R.id.tvintime);
        tv_out_time = (TextView) findViewById(R.id.tvouttime);
        rel_intime = (RelativeLayout) findViewById(R.id.rel_intime);
        rel_outtime = (RelativeLayout) findViewById(R.id.rel_outtime);
        fab_save = (FloatingActionButton) findViewById(R.id.fab);
        img_intime = (ImageView) findViewById(R.id.img_intime);
        img_outtime = (ImageView) findViewById(R.id.img_outtime);
        heading = (LinearLayout) findViewById(R.id.lay_heading);
        recyclerView = (RecyclerView) findViewById(R.id.rv_visitor);

        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        cpm = (RadioButton) findViewById(R.id.cpm);
        loreal = (RadioButton) findViewById(R.id.loreal);
        lr_cpm = (LinearLayout) findViewById(R.id.lr_cpm);
        lr_loreal = (LinearLayout) findViewById(R.id.lr_loreal);
        linear_empl = (LinearLayout) findViewById(R.id.linear_empl);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_designation = (EditText) findViewById(R.id.ed_designation);
        btncleardata = (Button) findViewById(R.id.btncleardata);
        loreal_clear = (Button) findViewById(R.id.loreal_clear);
        lay_feedback = (LinearLayout) findViewById(R.id.lay_feedback);
        feedback = (Button) findViewById(R.id.feedback);

        img_intime_loreal = (ImageView) findViewById(R.id.img_intime_loreal);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        database = new GODREJDatabase(this);
        database.open();

        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        tv_user_id.setText("Current User - " + username);

        str = CommonString.FILE_PATH;


        fab_save.setOnClickListener(this);
        rel_intime.setOnClickListener(this);
        rel_outtime.setOnClickListener(this);
        btnclear.setOnClickListener(this);
        btngo.setOnClickListener(this);
        btncleardata.setOnClickListener(this);
        loreal_clear.setOnClickListener(this);
        feedback.setOnClickListener(this);
        Path = CommonString.FILE_PATH;

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cpm) {
                    lr_cpm.setVisibility(View.VISIBLE);
                    lr_loreal.setVisibility(View.GONE);
                    linear_empl.setVisibility(View.VISIBLE);
                    check = "CPM";
                    lay_feedback.setVisibility(View.GONE);


                } else if (checkedId == R.id.loreal) {
                    lr_loreal.setVisibility(View.VISIBLE);
                    lr_cpm.setVisibility(View.GONE);
                    linear_empl.setVisibility(View.GONE);
                    check = "LOREAL";


                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        int id = v.getId();

        switch (id) {
            case R.id.fab:
                name = tvname.getText().toString();
                designation = tvdesignation.getText().toString();
                if (check_condition()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            VisitorLoginActivity.this).setTitle("Parinaam");
                    builder.setMessage("Are you sure you want to save the data ?")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            if (isUpdate) {

                                                String out_time = tv_out_time.getText().toString();
                                                visitorLoginGetterSetter.setOut_time(out_time);

                                                database.open();
                                                database.updateOutTimeVisitorLoginData(visitorLoginGetterSetter.getOut_time_img(), out_time, String.valueOf(visitorLoginGetterSetter.getEmp_code()), username, check, visitorLoginGetterSetter.getName());

                                                if (CheckNetAvailability()) {
                                                    new UploadVisitorData().execute();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "No internet connection! try again later", Toast.LENGTH_LONG).show();
                                                    clearVisitorData();
                                                    setLoginData();
                                                }

                                            } else {

                                                visitorLoginGetterSetter = new VisitorDetailGetterSetter();
                                                visitorLoginGetterSetter.setUsername(username);
                                                visitorLoginGetterSetter.setName(name);
                                                visitorLoginGetterSetter.setDesignation(designation);
                                                visitorLoginGetterSetter.setVisit_date(visit_date);
                                                visitorLoginGetterSetter.setIn_time_img(intime_img);
                                                visitorLoginGetterSetter.setIsexit(check);
                                                visitorLoginGetterSetter.setIn_time(tv_in_time.getText().toString());
                                                String out_time = tv_out_time.getText().toString();
                                                if (out_time.equalsIgnoreCase("Out Time")) {
                                                    visitorLoginGetterSetter.setOut_time("");
                                                } else {
                                                    visitorLoginGetterSetter.setOut_time(out_time);
                                                }


                                                visitorLoginGetterSetter.setEmp_code(empid);
                                                if (outtime_img == null) {
                                                    visitorLoginGetterSetter.setOut_time_img("");
                                                } else {
                                                    visitorLoginGetterSetter.setOut_time_img(outtime_img);
                                                }

                                                visitorLoginData.add(visitorLoginGetterSetter);
                                                database.open();

                                                database.InsertVisitorLogindata(visitorLoginGetterSetter);

                                                clearVisitorData();

                                                setLoginData();

                                            }


                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    if (error_msg.equals("Employee ID already entered")) {
                        clearVisitorData();
                    }
                    Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.rel_intime:

                if (check.equalsIgnoreCase("CPM")) {
                    if (et_emp_name.getText().toString() != null && !et_emp_name.getText().toString().equalsIgnoreCase("")) {
                        camin_clicked = true;
                        _pathforcheck = username + getCurrentTime() + "visitor_intime" + ".jpg";
                        _path = str + _pathforcheck;
                        CommonFunctions.startAnncaCameraActivity((Activity) context, _path);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill employee code first", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if ((ed_name.getText().toString() != null && !ed_name.getText().toString().equalsIgnoreCase("")) || (ed_designation.getText().toString() != null && !ed_designation.getText().toString().equalsIgnoreCase(""))) {
                        camin_clicked = true;
                        _pathforcheck = username + getCurrentTime() + "visitor_intime" + ".jpg";
                        _path = str + _pathforcheck;
                        CommonFunctions.startAnncaCameraActivity((Activity) context, _path);
                    } else {

                        Toast.makeText(getApplicationContext(), "Please fill Visiter Name and Designation", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case R.id.rel_outtime:

                if (!isUpdate) {
                    error_msg = "Please click Out Time image at out time";
                    Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();

                } else {
                    camout_clicked = true;
                    _pathforcheck = username + getCurrentTime() + "visitor_outtime" + ".jpg";
                    _path = str + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity((Activity) context, _path);
                }

                break;
            case R.id.btngo:

                emp_name = et_emp_name.getText().toString().toUpperCase().trim().replaceAll("[&^<>{}'$]", "");
                if (emp_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter Employee Name", Toast.LENGTH_SHORT).show();
                } else if (CheckNetAvailability()) {
                    new GetCredentials().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection! try again later", Toast.LENGTH_LONG).show();
                }

                break;


            case R.id.btnClear:

                clearVisitorData();

                break;
            case R.id.btncleardata:
                clearVisitorData();

                break;
            case R.id.loreal_clear:

                clearVisitorData();

                break;
            case R.id.feedback:

                popup(global_key_id);

                break;


        }

    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.DAY_OF_MONTH) + "" + m_cal.get(Calendar.MONTH) + "" + m_cal.get(Calendar.YEAR) + "" + m_cal.get(Calendar.HOUR_OF_DAY) + ""
                + m_cal.get(Calendar.MINUTE) + "" + m_cal.get(Calendar.SECOND);

        return intime;

    }

    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        return connected;
    }


    class GetCredentials extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(VisitorLoginActivity.this);
            dialog.setTitle("Employee Id");
            dialog.setMessage("Fetching....");
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                // JCP

                XmlPullParserFactory factory = XmlPullParserFactory
                        .newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                SoapObject request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", emp_name);
                request.addProperty("Type", "VISITOR_SEARCH");

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL, CommonString.TIMEOUT);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);


                Object result = (Object) envelope.getResponse();

                // for failure
                xpp.setInput(new StringReader(result.toString()));
                xpp.next();
                eventType = xpp.getEventType();

                visitordata = XMLHandlers.visitorDataXML(xpp, eventType);

                if (visitordata.getEMP_CD().size() == 0) {
                    return CommonString.KEY_NO_DATA;
                } else {
                    return CommonString.KEY_SUCCESS;
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return CommonString.MESSAGE_INVALID_XML;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return CommonString.MESSAGE_EXCEPTION;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.cancel();

            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                dialog.dismiss();
                if (visitordata != null && visitordata.getEMP_CD().size() > 0) {
                    tvname.setText(visitordata.getEMPLOYEE().get(0));
                    tvdesignation.setText(visitordata.getDESIGNATION().get(0));
                    empid = String.valueOf(visitordata.getEMP_CD().get(0));
                    name = visitordata.getEMPLOYEE().get(0);
                    designation = visitordata.getDESIGNATION().get(0);

                    btngo.setVisibility(View.GONE);
                    btncleardata.setVisibility(View.VISIBLE);
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Please enter valid Employee Code", Toast.LENGTH_LONG).show();
                }

            } else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error : " + result, Toast.LENGTH_LONG).show();
            }


        }

    }


    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        List<VisitorDetailGetterSetter> data = Collections.emptyList();

        public MyRecyclerAdapter(Context context, List<VisitorDetailGetterSetter> data) {

            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_visitor_search, parent, false);

            MyRecyclerAdapter.MyViewHolder holder = new MyRecyclerAdapter.MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            final VisitorDetailGetterSetter current = data.get(position);

            holder.name.setText(current.getName());
            holder.designation.setText(current.getDesignation());
            holder.emp_code.setText(current.getEmp_code());

            holder.parent_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvname.setText(current.getName());
                    tvdesignation.setText(current.getDesignation());
                    empid = current.getEmp_code();
                    name = current.getName();
                    designation = current.getDesignation();
                    dialog_list.cancel();

                }
            });
        }

        @Override
        public int getItemCount() {
            return visitorListData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name, designation, emp_code;
            LinearLayout parent_layout;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_name);
                designation = (TextView) itemView.findViewById(R.id.tv_designation);
                emp_code = (TextView) itemView.findViewById(R.id.tv_emp_code);
                parent_layout = (LinearLayout) itemView.findViewById(R.id.layout_parent);

            }

        }
    }

    public void clearVisitorData() {
        lay_feedback.setVisibility(View.GONE);
        ed_name.setText("");
        ed_designation.setText("");
        et_emp_name.setText("");
        tvname.setText("");
        tvdesignation.setText("");
        tv_in_time.setText("");
        tv_out_time.setText("");

        img_intime.setVisibility(View.GONE);
        img_outtime.setVisibility(View.GONE);

        rel_intime.setVisibility(View.VISIBLE);
        rel_outtime.setVisibility(View.VISIBLE);

        empid = null;
        name = null;
        designation = null;
        intime_img = null;
        outtime_img = null;
        fab_save.setClickable(true);
        fab_save.setVisibility(View.VISIBLE);
        rel_intime.setClickable(true);
        rel_outtime.setClickable(true);

        btngo.setVisibility(View.VISIBLE);
        btncleardata.setVisibility(View.GONE);
        ed_name.setEnabled(true);
        ed_designation.setEnabled(true);

        //image
        rel_intime.setVisibility(View.VISIBLE);
        rel_intime.setClickable(true);
        img_intime.setVisibility(View.GONE);

        isUpdate = false;

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
                    if (new File(str + _pathforcheck).exists()) {
                        String metadata = CommonFunctions.setmetadataforattendance("Visitor Image", username);
                        CommonFunctions.addMetadataAndTimeStampToImage(this, _path, metadata, visit_date);
                        if (camin_clicked) {
                            intime_img = _pathforcheck;
                            tv_in_time.setText(getClicktime());
                            _pathforcheck = "";
                            camin_clicked = false;
                            rel_intime.setVisibility(View.GONE);
                            rel_intime.setClickable(false);
                            setScaledImage(img_intime, _path);
                            img_intime.setVisibility(View.VISIBLE);

                        } else if (camout_clicked) {
                            visitorLoginGetterSetter.setOut_time_img(_pathforcheck);
                            tv_out_time.setText(getClicktime());
                            _pathforcheck = "";
                            camout_clicked = false;
                            rel_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(false);
                            img_outtime.setVisibility(View.VISIBLE);
                            setScaledImage(img_outtime, _path);
                        }

                        break;

                    }
                }

                break;
        }
    }

    public String getClicktime() {
        Calendar m_cal = Calendar.getInstance();
        String time = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return time;
    }

    private void setScaledImage(ImageView imageView, final String path) {
        final ImageView iv = imageView;
        ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = iv.getMeasuredHeight();
                int imageViewWidth = iv.getMeasuredWidth();
                iv.setImageBitmap(convertBitmap(path, imageViewWidth, imageViewHeight));
                return true;
            }
        });
    }


    public static Bitmap convertBitmap(String path, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        BitmapFactory.Options ourOptions = new BitmapFactory.Options();
        ourOptions.inDither = false;
        ourOptions.inPurgeable = true;
        ourOptions.inInputShareable = true;
        ourOptions.inTempStorage = new byte[32 * 1024];
        ourOptions.inSampleSize = calculateInSampleSize(ourOptions, reqWidth, reqHeight);
        File file = new File(path);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, ourOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public boolean check_condition() {


        if (check.equalsIgnoreCase("CPM")) {
            if (isUpdate) {
                if (visitorLoginGetterSetter.getOut_time_img().equals("")) {
                    error_msg = "Please click out time image";
                    return false;
                } else {
                    return true;
                }
            } else {
                if (empid == null || name == null || designation == null) {
                    error_msg = "Please Select Employee";
                    return false;
                } else if (intime_img == null) {
                    error_msg = "Please click in time image";
                    return false;
                } else if (database.isVistorDataExists(empid, visit_date)) {
                    error_msg = "Employee already entered";
                    return false;
                } else {
                    return true;
                }
            }

        } else {
            if (isUpdate) {
                if (visitorLoginGetterSetter.getOut_time_img().equals("")) {
                    error_msg = "Please click out time image";
                    return false;
                } else {
                    return true;
                }
            } else {
                name = ed_name.getText().toString().trim().replaceAll("[&^<>{}'$]", "");
                designation = ed_designation.getText().toString().trim().replaceAll("[&^<>{}'$]", "");
                if (name.equalsIgnoreCase("") || designation.equalsIgnoreCase("")) {
                    error_msg = "Please fill Visitor Name and Designation";
                    return false;
                } else if (intime_img == null) {
                    error_msg = "Please click in time image";
                    return false;
                } else if (database.isVistorDataExists(empid, visit_date)) {
                    error_msg = "Employee already entered";
                    return false;
                } else {
                    return true;
                }
            }

        }

    }

    public void setLoginData() {
        database.open();
        visitorLoginData = database.getVisitorLoginData(visit_date, username);

        if (visitorLoginData.size() > 0) {

            heading.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext()));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            heading.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }


    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        Context context;
        LayoutInflater inflater;

        RecyclerAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.child_visitor_login, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.tv_name.setText(visitorLoginData.get(position).getName());
            holder.tv_intime.setText(visitorLoginData.get(position).getIn_time());
            holder.tv_outtime.setText(visitorLoginData.get(position).getOut_time());
            if (visitorLoginData.get(position).getUpload_status() != null && visitorLoginData.get(position).getUpload_status().equals("U")) {
                holder.img_upload_tick.setVisibility(View.VISIBLE);
            } else {
                holder.img_upload_tick.setVisibility(View.INVISIBLE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    visitorLoginGetterSetter = visitorLoginData.get(position);

                    if (visitorLoginGetterSetter.getUpload_status() != null && visitorLoginGetterSetter.getUpload_status().equalsIgnoreCase("U")) {
                        Toast.makeText(getApplicationContext(), "Data already uploaded", Toast.LENGTH_SHORT).show();
                    } else {

                        if (!visitorLoginGetterSetter.getIsexit().equalsIgnoreCase("") && visitorLoginGetterSetter.getIsexit().equalsIgnoreCase("CPM")) {
                            cpm.setChecked(true);
                            tvname.setText(visitorLoginGetterSetter.getName());
                            tvdesignation.setText(visitorLoginGetterSetter.getDesignation());
                            btncleardata.setVisibility(View.VISIBLE);
                            btngo.setVisibility(View.GONE);


                        }
                        if (!visitorLoginGetterSetter.getIsexit().equalsIgnoreCase("") && visitorLoginGetterSetter.getIsexit().equalsIgnoreCase("LOREAL")) {
                            loreal.setChecked(true);
                            ed_name.setText(visitorLoginGetterSetter.getName());
                            ed_designation.setText(visitorLoginGetterSetter.getDesignation());
                            ed_name.setEnabled(false);
                            ed_designation.setEnabled(false);
                            btncleardata.setVisibility(View.VISIBLE);
                            global_key_id = visitorLoginGetterSetter.getKey_id();
                            btngo.setVisibility(View.GONE);
                            lay_feedback.setVisibility(View.VISIBLE);

                        }

                        tv_in_time.setText(visitorLoginGetterSetter.getIn_time());
                        String intime_img = visitorLoginGetterSetter.getIn_time_img();
                        if (intime_img != null && !intime_img.equals("")) {

                            rel_intime.setVisibility(View.GONE);
                            rel_intime.setClickable(false);
                            setScaledImage(img_intime, str + intime_img);
                            img_intime.setVisibility(View.VISIBLE);
                        }

                        String outtime_img = visitorLoginGetterSetter.getOut_time_img();
                        if (outtime_img != null && !outtime_img.equals("")) {

                            rel_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(false);
                            tv_out_time.setText(visitorLoginGetterSetter.getOut_time());
                            setScaledImage(img_outtime, str + outtime_img);
                            img_outtime.setVisibility(View.VISIBLE);
                        } else {
                            tv_out_time.setText(visitorLoginGetterSetter.getOut_time());
                            rel_outtime.setVisibility(View.VISIBLE);
                            img_outtime.setVisibility(View.GONE);
                            rel_outtime.setClickable(true);
                        }
                        //  fab_save.setText("Upload");
                        if (!visitorLoginGetterSetter.getOut_time().equals("")) {
                            fab_save.setClickable(true);
                            fab_save.setVisibility(View.VISIBLE);
                        }
                        isUpdate = true;

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return visitorLoginData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_name;
            TextView tv_intime;
            TextView tv_outtime;
            LinearLayout layout;
            ImageView img_upload_tick;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_intime = (TextView) itemView.findViewById(R.id.tv_intime);
                tv_outtime = (TextView) itemView.findViewById(R.id.tv_outtime);
                layout = (LinearLayout) itemView.findViewById(R.id.ll_item);
                img_upload_tick = (ImageView) itemView.findViewById(R.id.img_upload_tick);
            }
        }
    }

    //upload Visitor data
    class UploadVisitorData extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new ProgressDialog(VisitorLoginActivity.this);
            dialog.setTitle("Visitor data");
            dialog.setMessage("Uploading....");
            dialog.setCancelable(false);
            dialog.show();


        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                UploadImageWithRetrofit uploadRetro = new UploadImageWithRetrofit(context);

                feedback_ques = database.getInsertFeedbackUploadData(global_key_id);
                String feedbackxml = "";
                String onXML = "";
                if (feedback_ques.size() > 0) {
                    for (int k = 0; k < feedback_ques.size(); k++) {
                        onXML = "[LOREAL_FEEDBACK]"
                                + "[COMMAN_ID]"
                                + feedback_ques.get(k).getKey_id()
                                + "[/COMMAN_ID]"
                                + "[FEEDBACK]"
                                + feedback_ques.get(k).getFeedback()
                                + "[/FEEDBACK]"
                                + "[FEEDBACK_CD]"
                                + feedback_ques.get(k).getFeedback_cd()
                                + "[/FEEDBACK_CD]"
                                + "[RATING]"
                                + feedback_ques.get(k).getRating()
                                + "[/RATING]"
                                + "[REMARK]"
                                + feedback_ques.get(k).getRemark()
                                + "[/REMARK]"
                                + "[/LOREAL_FEEDBACK]";
                        feedbackxml = feedbackxml + onXML;
                    }
                }

                String visitdata = "[USER_DATA][CREATED_BY]"
                        + username
                        + "[/CREATED_BY][EMP_ID]"
                        + visitorLoginGetterSetter.getEmp_code()
                        + "[/EMP_ID][VISIT_DATE]"
                        + visit_date
                        + "[/VISIT_DATE][IN_TIME_IMAGE]"
                        + visitorLoginGetterSetter.getIn_time_img()
                        + "[/IN_TIME_IMAGE][OUT_TIME_IMAGE]"
                        + visitorLoginGetterSetter.getOut_time_img()
                        + "[/OUT_TIME_IMAGE][IN_TIME]"
                        + visitorLoginGetterSetter.getIn_time()
                        + "[/IN_TIME]"
                        + "[OUT_TIME]"
                        + visitorLoginGetterSetter.getOut_time()
                        + "[/OUT_TIME]"
                        + "[EXIT]"
                        + check
                        + "[/EXIT]"
                        + "[VISITER_NAME]"
                        + visitorLoginGetterSetter.getName()
                        + "[/VISITER_NAME]"
                        + "[DESIGNATION]"
                        + visitorLoginGetterSetter.getDesignation()
                        + "[/DESIGNATION]"
                       /* + "[COMMAN_ID]"
                        + visitorLoginGetterSetter.getKey_id()
                        + "[/COMMAN_ID]"
                        + feedbackxml*/
                        + "[/USER_DATA]";

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_XML);
                request.addProperty("XMLDATA", visitdata);
                request.addProperty("KEYS", "VISITOR_LOGIN_NEW");
                request.addProperty("USERNAME", username);
                request.addProperty("MID", 0);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_XML, envelope);

                Object result = (Object) envelope.getResponse();

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                    return "Failure";

                }

                if (visitorLoginGetterSetter.getIn_time_img() != null && !visitorLoginGetterSetter.getIn_time_img().equals("")) {
                    if (new File(CommonString.FILE_PATH + visitorLoginGetterSetter.getIn_time_img()).exists()) {
                        result = uploadImageRecursiveWithIndex(visitorLoginGetterSetter.getIn_time_img(), "Visitor");
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                return "Visitor Images";
                            } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return "Visitor Images";
                            } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                throw new IOException();
                            }
                        }
                        runOnUiThread(new Runnable() {

                            public void run() {
                                // message.setText("Visitor Images Uploaded");
                            }
                        });

                    }
                }

                if (visitorLoginGetterSetter.getOut_time_img() != null && !visitorLoginGetterSetter.getOut_time_img().equals("")) {
                    if (new File(CommonString.FILE_PATH + visitorLoginGetterSetter.getOut_time_img()).exists()) {
                        result = uploadImageRecursiveWithIndex(visitorLoginGetterSetter.getOut_time_img(), "Visitor");
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                return "Visitor Images";
                            } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return "Visitor Images";
                            } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                                throw new IOException();
                            }
                        }

                        runOnUiThread(new Runnable() {

                            public void run() {
                                // message.setText("Visitor Images Uploaded");
                            }
                        });

                    }
                }


                return CommonString.KEY_SUCCESS;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if ((dialog != null) && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result.equals(CommonString.KEY_SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Visit Data Uploaded", Toast.LENGTH_LONG).show();
                //update upload_status to U
                database.open();
                database.updateVisitorUploadData(visitorLoginGetterSetter.getEmp_code(), visitorLoginGetterSetter.getName(), check);
                clearVisitorData();
                setLoginData();
                global_key_id = "0";
            } else {
                Toast.makeText(getApplicationContext(), "Data not uploaded!", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setLoginData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


    private void popup(final String key_id) {

        dialog1 = new Dialog(VisitorLoginActivity.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_loreal);
        ed_feedback = (EditText) dialog1.findViewById(R.id.feedback);
        save = (Button) dialog1.findViewById(R.id.save);
        lv_feedback = (RecyclerView) dialog1.findViewById(R.id.lv_routewise);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(true);
        database.open();
        feedback_ques = database.getInsertFeedbackData(key_id);
        for (int i = 0; i < feedback_ques.size(); i++) {
            ed_feedback.setText(feedback_ques.get(i).getRemark());

        }

        if (feedback_ques.size() > 0) {
            feedback_question = new RouteAdapter(getApplicationContext(), feedback_ques);
            lv_feedback.setAdapter(feedback_question);
            lv_feedback.setLayoutManager(new LinearLayoutManager(VisitorLoginActivity.this));
        } else {
            feedback_ques = database.getFeedaback_question();
            if (feedback_ques.size() > 0) {
                feedback_question = new RouteAdapter(getApplicationContext(), feedback_ques);
                lv_feedback.setAdapter(feedback_question);
                lv_feedback.setLayoutManager(new LinearLayoutManager(this));

            }
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        VisitorLoginActivity.this);
                builder.setMessage("Do you want to save the data ")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        String remark1 = ed_feedback.getText().toString().replaceAll("[(!@#$%^&*?)\"]", " ").trim();
                                        visitorLoginGetterSetter.setRemark(remark1);

                                        long id1 = database.InsertFeedbackData(feedback_ques, check, visit_date, remark1, visitorLoginGetterSetter.getName(), visitorLoginGetterSetter.getDesignation(), key_id);
                                        if (id1 > 0) {
                                            Snackbar.make(fab_save, "Data saved successfully", Snackbar.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            dialog1.dismiss();
                                            //   global_key_id = "";
                                            //setLoginData();

                                        } else {
                                            Snackbar.make(fab_save, "Data not saved", Snackbar.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        dialog1.dismiss();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        dialog1.show();
    }

    public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<VisitorDetailGetterSetter> data = Collections.emptyList();

        public RouteAdapter(Context context, List<VisitorDetailGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public RouteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.dialog_list_, parent, false);
            RouteAdapter.MyViewHolder holder = new RouteAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RouteAdapter.MyViewHolder viewHolder, final int position) {

            final VisitorDetailGetterSetter current = data.get(position);

            viewHolder.question.setText(current.getFeedback());

            //for reason spinner
            final ArrayList<VisitorDetailGetterSetter> reason_list = database.getRatingData();
            VisitorDetailGetterSetter non = new VisitorDetailGetterSetter();
            non.setAreason("-Select Reason-");
            //  non.setRating("0");
            reason_list.add(0, non);

            viewHolder.rating_sp.setAdapter(new ReasonSpinnerAdapter(VisitorLoginActivity.this, R.layout.spinner_text_view, reason_list));
            for (int i = 0; i < reason_list.size(); i++) {
                if (reason_list.get(i).getRating().equals(current.getRating())) {
                    viewHolder.rating_sp.setSelection(i);
                    break;
                }
            }

            viewHolder.rating_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        String ans = reason_list.get(pos).getRating().toString();
                        current.setRating(ans);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView question;
            Spinner rating_sp;

            public MyViewHolder(View itemView) {
                super(itemView);
                question = (TextView) itemView.findViewById(R.id.question);
                rating_sp = (Spinner) itemView.findViewById(R.id.rating_sp);
            }
        }
    }

    public class ReasonSpinnerAdapter extends ArrayAdapter<VisitorDetailGetterSetter> {
        List<VisitorDetailGetterSetter> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<VisitorDetailGetterSetter> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);

            VisitorDetailGetterSetter cm = list.get(position);

            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(list.get(position).getRating());

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);

            VisitorDetailGetterSetter cm = list.get(position);

            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(cm.getRating());

            return view;
        }

    }

    public boolean uploadImageRecursiveWithIndex(final String filelist, String folder_name) {
        final boolean[] status = {true};
        File originalFile = new File(CommonString.FILE_PATH + filelist);
        com.squareup.okhttp.OkHttpClient okHttpClient = new com.squareup.okhttp.OkHttpClient();
        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        //bitmap
        File finalFile = saveBitmapToFile(originalFile);
        if (finalFile == null) {
            finalFile = originalFile;
        }

        RequestBody photo = RequestBody.create(com.squareup.okhttp.MediaType.parse("application/octet-stream"), finalFile);
        String date = visit_date.replace("/", "");

        com.squareup.okhttp.RequestBody body1 = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("file", finalFile.getName(),
                photo).addFormDataPart("FolderName", folder_name).addFormDataPart("Path", date).build();
        Retrofit adapter = new retrofit.Retrofit.Builder().baseUrl(CommonString.URL + "/").client(okHttpClient).addConverterFactory(new StringConverterFactory()).build();

        //interface
        PostApi api = adapter.create(PostApi.class);
        Call<String> call = api.getUploadImageRetrofitOne(body1);
        final File finalFile1 = finalFile;
        call.enqueue(new retrofit.Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response) {
                if (response.isSuccess() && response.body().contains("Success") && response.code() == 200) {
                    finalFile1.delete();
                } else {
                    status[0] = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                    status[0] = false;
                }
            }
        });

        return status[0];
    }

}
