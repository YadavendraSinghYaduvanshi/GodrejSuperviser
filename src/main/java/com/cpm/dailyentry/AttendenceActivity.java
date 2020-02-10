package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.GpsTracker.GPSTracker;
import com.cpm.godrejsupervisor.R;
import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendenceActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_cam, img_clicked;
    Button btn_save, btn_preview;
    // File file;
    String _pathforcheck = null, _path = null, str, latitud, longitud, app_ver;
    String imgDecodableString, pathnew;
    String store_cd, visit_date, username, intime, store_id, _UserId;
    Uri outputFileUri;
    private SharedPreferences preferences;
    final static int CAMERA_OUTPUT = 0;
    AlertDialog alert;
    ArrayList<JourneyPlanGetterSetter> jcp;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private LocationManager locmanager = null;
    private double latitude = 0.0, longitude = 0.0;
    Location location;
    GODREJDatabase db;

    File file1 = null;
    File file = null, file2 = null, file3, file4;
    public static int MY_REQUEST_CODE = 1;
    TextView textview;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    private ArrayList<CoverageBean> list1 = new ArrayList<CoverageBean>();
    boolean flagimage = false;
    String datacheck = "";
    String[] words;
    String validity;
    boolean previewima = false;
    boolean flagcancel = false;
    private static int LOAD_IMAGE_RESULTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new GODREJDatabase(this);
        db.open();
        img_cam = (ImageView) findViewById(R.id.img_selfie);
        img_clicked = (ImageView) findViewById(R.id.img_cam_selfie);
        textview = (TextView) findViewById(R.id.testvi);
        btn_save = (Button) findViewById(R.id.btn_save_selfie);
        btn_preview = (Button) findViewById(R.id.btn_Preview);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        _UserId = preferences.getString(CommonString.KEY_USER_ID, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        intime = getCurrentTime();
        str = CommonString.FILE_PATH;
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        GPSTracker gps = new GPSTracker(AttendenceActivity.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();
        latitud = String.valueOf(latitude);
        longitud = String.valueOf(longitude);
        checkInTimemethod();
        img_clicked.setOnClickListener(this);
        img_cam.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
    }

    protected void checkInTimemethod() {
        list1 = db.getCoverageDataReason(visit_date, store_cd);
        btn_preview.setVisibility(ImageView.INVISIBLE);
        if (list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).getInTime() != null) {
                    btn_save.setText("Take Out Time ");
                    flagimage = true;
                    btn_preview.setVisibility(ImageView.VISIBLE);
                    break;
                }

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_cam_selfie:
                if (flagimage == true) {
                    _pathforcheck = "attendanceimageOutTime" + store_cd + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";
                    _path = Environment.getExternalStorageDirectory() + "/Godrejsupervisor/" + _pathforcheck;
                    startCameraActivity();
                    break;
                } else {
                    _pathforcheck = "attendanceimageInTime" + store_cd + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";
                    _path = Environment.getExternalStorageDirectory() + "/Godrejsupervisor/" + _pathforcheck;
                    startCameraActivity();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pathimage", str + _pathforcheck);
                    editor.commit();
                    break;
                }

            case R.id.img_selfie:
                if (flagimage == true) {
                    _pathforcheck = "attendanceimageOutTime" + store_cd + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";
                    _path = Environment.getExternalStorageDirectory() + "/Godrejsupervisor/" + _pathforcheck;
                    startCameraActivity();
                    break;
                } else {
                    _pathforcheck = "attendanceimageInTime" + store_cd + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";
                    _path = Environment.getExternalStorageDirectory() + "/Godrejsupervisor/" + _pathforcheck;
                    startCameraActivity();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pathimage", str + _pathforcheck);
                    editor.commit();
                    break;
                }

            case R.id.btn_save_selfie:
                if (file3 != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendenceActivity.this);
                    builder.setMessage("Do you want to save the data ")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            CoverageBean cdata = new CoverageBean();
                                            cdata.setOutTime(getCurrentTime());
                                            cdata.setImage02(_pathforcheck);
                                            finish();
                                        }


                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    alert = builder.create();
                    alert.show();

                    break;
                } else if (file4 != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            AttendenceActivity.this);
                    builder.setMessage("Do you want to save the data ")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                            CoverageBean cdata = new CoverageBean();
                                            cdata.setStoreId(store_cd);
                                            cdata.setVisitDate(visit_date);
                                            cdata.setUserId(username);
                                            cdata.setInTime(intime);
                                            // cdata.setOutTime(getCurrentTime());
                                            cdata.setReason("");
                                            cdata.setReasonid("0");
                                            cdata.setLatitude(latitud);
                                            cdata.setLongitude(longitud);
                                            cdata.setImage(_pathforcheck);

                                            //  cdata.setImage1("");
                                            db.InsertCoverageData(cdata);
                                            Intent i = new Intent(AttendenceActivity.this, DailyEntryScreen.class);
                                            startActivity(i);
                                            finish();

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
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "Please Take A Selfie", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_Preview:
                pathnew = preferences.getString("pathimage", null);
                if (previewima == true) {
                    btn_save.setVisibility(View.VISIBLE);
                    // flagcancel=true;
                    btn_preview.setText("Preview In Time");
                    textview.setText("Click your selfie");
                    img_clicked.setVisibility(ImageView.VISIBLE);
                    img_cam.setVisibility(ImageView.INVISIBLE);
                    previewima = false;

                } else {

                    btn_preview.setText("Cancel");

                    textview.setText("Preview In-Time Image");
                    btn_save.setVisibility(View.INVISIBLE);
                    previewimage(pathnew);
                }
                break;
        }
    }

    protected void startCameraActivity() {
        try {
            if (flagimage == true) {
                Log.i("MakeMachine", "startCameraActivity()");
                file1 = new File(_path);
                outputFileUri = Uri.fromFile(file1);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, CAMERA_OUTPUT);
            } else {
                Log.i("MakeMachine", "startCameraActivity()");
                file = new File(_path);
                outputFileUri = Uri.fromFile(file);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, CAMERA_OUTPUT);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                img_clicked.setVisibility(ImageView.INVISIBLE);
                img_cam.setVisibility(ImageView.VISIBLE);
                if (flagimage == true) {
                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
                        if (new File(str + _pathforcheck).exists()) {
                            file3 = new File(str + _pathforcheck);
                            Uri uri = Uri.fromFile(file3);
                            Bitmap bitmap;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                                img_cam.setImageBitmap(bitmap);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            img_clicked.setVisibility(ImageView.INVISIBLE);
                            img_cam.setVisibility(ImageView.VISIBLE);
                            btn_preview.setVisibility(ImageView.INVISIBLE);

                        }
                    }
                } else {

                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
                        if (new File(str + _pathforcheck).exists()) {


                            file4 = new File(str + _pathforcheck);

                            Uri uri = Uri.fromFile(file4);
                            Bitmap bitmap;

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                                img_cam.setImageBitmap(bitmap);
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                            img_clicked.setVisibility(ImageView.INVISIBLE);
                            img_cam.setVisibility(ImageView.VISIBLE);
                            btn_preview.setVisibility(ImageView.INVISIBLE);

                        }
                    }
                }


                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public static Bitmap crupAndScale(Bitmap source, int scale) {
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight() : source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight() : source.getWidth();
        int x = source.getHeight() >= source.getWidth() ? 0 : (longer - factor) / 2;
        int y = source.getHeight() <= source.getWidth() ? 0 : (longer - factor) / 2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    public void previewimage(String pathimage) {
        if (pathimage != null) {
            file1 = new File(pathimage);
            Uri uri = Uri.fromFile(file1);
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = crupAndScale(bitmap, 800); // if you mind scaling
                img_cam.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            img_clicked.setVisibility(ImageView.INVISIBLE);
            img_cam.setVisibility(ImageView.VISIBLE);
            previewima = true;
        } else {
            Toast.makeText(getApplicationContext(), "NO In Time Preview", Toast.LENGTH_SHORT).show();
        }
    }



}
