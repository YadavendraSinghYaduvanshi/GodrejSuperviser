package com.cpm.GodrejSupervisor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.dailyentry.VisitorLoginActivity;
import com.cpm.godrejsupervisor.R;
import com.cpm.dailyentry.StoreListUsingSerarchActivrty;
import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.fragment.HelpFragment;
import com.cpm.fragment.MainFragment;
import com.cpm.upload.CheckoutNUpload;
import com.cpm.upload.UploadDataActivity;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GODREJDatabase database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    private SharedPreferences preferences = null;
    private String date, user_name, user_type;

    TextView tv_username, tv_usertype;

    FrameLayout frameLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main_menu2, navigationView, false);
        navigationView.addHeaderView(headerView);
        tv_username = (TextView) headerView.findViewById(R.id.nav_user_name);
        tv_usertype = (TextView) headerView.findViewById(R.id.nav_user_type);
        tv_username.setText(user_name);
        tv_usertype.setText(user_type);
        tv_username.setTextColor(Color.BLACK);
        tv_usertype.setTextColor(Color.BLACK);
        navigationView.setNavigationItemSelectedListener(this);
        database = new GODREJDatabase(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_daily) {
            // Handle the camera action
            //  Intent startDownload = 	new Intent(this,DailyEntryScreen.class);
            Intent startDownload = new Intent(this, StoreListUsingSerarchActivrty.class);
            startActivity(startDownload);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        } else if (id == R.id.nav_download) {
            if (checkNetIsAvailable()) {
                if (database.isCoverageDataFilled(date)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam");
                    builder.setMessage("Please Upload Previous Data First")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent startUpload = new Intent(MainMenuActivity.this, CheckoutNUpload.class);
                                    startActivity(startUpload);
                                    finish();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Intent startDownload = new Intent(getApplicationContext(), CompleteDownloadActivity.class);
                    startActivity(startDownload);
                    finish();
                }
            } else {
                Snackbar.make(frameLayout, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.nav_upload) {
            if (checkNetIsAvailable()) {
                jcplist = database.getJCPData(date);
                ArrayList<CoverageBean> cdata = new ArrayList<CoverageBean>();
                cdata = database.getCoverageData(date);
                boolean flag = true;
                if (jcplist.size() == 0) {
                    Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                } else {
                    if (cdata.size() > 0) {
                        for (int i = 0; i < cdata.size(); i++) {
                            if (cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_VALID)
                                    || cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                Snackbar.make(frameLayout, "First checkout of store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            if ((validate_data())) {
                                Intent i = new Intent(getBaseContext(), UploadDataActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                finish();
                            }
                        }
                    }
                }
            } else {
                Snackbar.make(frameLayout, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        } else if (id == R.id.visiter_login) {
            jcplist = database.getJCPData(date);
            if (jcplist.size() > 0) {
                Intent in = new Intent(this, VisitorLoginActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            } else {
                Snackbar.make(frameLayout, "Please Download Data First", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_export) {
            showExportDialog();
        } else if (id == R.id.nav_exit) {
            Intent startDownload = new Intent(this, LoginActivity.class);
            startActivity(startDownload);
            finish();
        } else if (id == R.id.nav_help) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            HelpFragment cartfrag = new HelpFragment();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create a Folder for Images
//        File file = new File(Environment.getExternalStorageDirectory(), "PNGsupervisor");
//        if (!file.isDirectory()) {
//            file.mkdir();
//        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment cartfrag = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();
    }

    public void showExportDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainMenuActivity.this);
        builder1.setMessage(R.string.Areyou_sure_take_backup)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            File file = new File(Environment.getExternalStorageDirectory(), "GODREJ_SUP_backup");
                            if (!file.isDirectory()) {
                                file.mkdir();
                            }

                            File sd = Environment.getExternalStorageDirectory();
                            File data = Environment.getDataDirectory();

                            if (sd.canWrite()) {
                                long date = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yy");
                                String dateString = sdf.format(date);
                                String currentDBPath = "//data//com.cpm.godrejsupervisor//databases//" + GODREJDatabase.DATABASE_NAME;
                                String backupDBPath = user_name + "_GODREJ_SUP_backup_" + dateString.replace('/', '_')
                                        + getCurrentTime().replace(":", "") + ".db";
                                String path = Environment.getExternalStorageDirectory().getPath() + "/GODREJ_SUP_backup";
                                File currentDB = new File(data, currentDBPath);
                                File backupDB = new File(path, backupDBPath);
                                if (currentDB.exists()) {
                                    @SuppressWarnings("resource")
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                }

                                File dir = new File(CommonString.BACKUP_FILE_PATH);
                                ArrayList<String> list = new ArrayList();
                                list = getFileNames(dir.listFiles());
                                if (list.size() > 0) {
                                    for (int i1 = 0; i1 < list.size(); i1++) {
                                        if (list.get(i1).contains("_GODREJ_SUP_backup_")) {
                                            File originalFile = new File(CommonString.BACKUP_FILE_PATH + list.get(i1));
                                            // uploadBackup(getActivity(), originalFile.getName(), "DBBackup");

                                        }
                                    }
                                }
                                Snackbar.make(frameLayout, "Database Exported And Uploaded Successfully", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }

    public boolean validate_data() {
        boolean result = false;
        database.open();
        ArrayList<CoverageBean> cdata;
        JourneyPlanGetterSetter storestatus;
        cdata = database.getCoverageData(date);
        if (cdata.size() > 0) {
            for (int i = 0; i < cdata.size(); i++) {
                storestatus = database.getStoreStatus(cdata.get(i).getStoreId());
                if (!storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                    if (storestatus.getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)
                            || storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_P) ||
                            cdata.get(i).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) ||
                            cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_C) ||
                            cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_P) ||
                            cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D) ||
                            storestatus.getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
                        result = true;
                        break;

                    }
                }
            }
        }

        return result;
    }

}
