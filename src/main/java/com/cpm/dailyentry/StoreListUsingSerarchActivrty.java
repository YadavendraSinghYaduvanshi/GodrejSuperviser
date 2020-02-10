package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.godrejsupervisor.R;
import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.message.AlertMessage;
import com.cpm.upload.CheckoutNUpload;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;
import com.crashlytics.android.Crashlytics;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by jeevanp on 3/23/2018.
 */

public class StoreListUsingSerarchActivrty extends AppCompatActivity implements
        AdapterView.OnItemClickListener, ObservableScrollViewCallbacks, View.OnClickListener {
    String visit_date, username, _UserId, app_ver, user_type, date;
    private SharedPreferences.Editor editor = null;
    LinearLayout parent_linear, nodata_linear;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    private SharedPreferences preferences;
    ArrayList<CoverageBean> coverage;
    AutoCompleteTextView autocomp_txt;
    JourneyPlanGetterSetter jcpData;
    boolean search_flag = true;
    FloatingActionButton fab;
    RelativeLayout nodata_rl;
    ObservableListView lv;
    ProgressDialog loading;
    GODREJDatabase database;
    EditText search_store;
    CardView card_autoc;
    ListView dialog_list;
    Dialog dialog;
    int eventType;
    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storesearchlistlayout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lv = (ObservableListView) findViewById(R.id.obserlist);
        nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
        parent_linear = (LinearLayout) findViewById(R.id.parent_linear);

        ////////for search store
        card_autoc = (CardView) findViewById(R.id.card_autoc);
        nodata_rl = (RelativeLayout) findViewById(R.id.nodata_rl);
        autocomp_txt = (AutoCompleteTextView) findViewById(R.id.serach_store_code);
        search_store = (EditText) findViewById(R.id.search_store);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        go = (Button) findViewById(R.id.go);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = new GODREJDatabase(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        _UserId = preferences.getString(CommonString.KEY_USER_ID, null);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        editor = preferences.edit();
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Store List - " + visit_date);
        lv.setScrollViewCallbacks(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data
                if (checkNetIsAvailable()) {
                    if (database.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListUsingSerarchActivrty.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage(R.string.previousupload)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent startUpload = new Intent(StoreListUsingSerarchActivrty.this, CheckoutNUpload.class);
                                        startActivity(startUpload);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        try {
                            database.open();
                            database.deletePreviousUploadedData(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                        Intent startDownload = new Intent(StoreListUsingSerarchActivrty.this, CompleteDownloadActivity.class);
                        startActivity(startDownload);
                        finish();
                    }
                } else {
                    Snackbar.make(lv, R.string.nonetwork, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });
        go.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.go:
                if (checkNetIsAvailable()) {
                    if (!autocomp_txt.getText().toString().isEmpty() || !search_store.getText().toString().isEmpty()) {
                        search_flag = false;
                        new BackgroundTask(this, autocomp_txt.getText().toString(), search_store.getText().toString()
                        ).execute();
                    } else {
                        Snackbar.make(lv, "Please enter 'Retailer code OR Store name'.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(lv, R.string.nonetwork, Snackbar.LENGTH_LONG).show();
                }
        }
    }

    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }


    public void onDownMotionEvent() {
    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        coverage = database.getCoveragDailyEntry(date);
        jcplist = database.getJCPData(date);
        ValidateConditionData();
        validateSearchOption();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jcplist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.tvstorename);
                holder.city = (TextView) convertView.findViewById(R.id.tvcity);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.tvkeyaccount);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.checkout = (Button) convertView.findViewById(R.id.chkout);
                holder.checkinclose = (ImageView) convertView.findViewById(R.id.closechkin);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreListUsingSerarchActivrty.this).setTitle(getString(R.string.parinaam));
                    builder.setMessage("Are you sure you want to Checkout ?")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            if (CheckNetAvailability()) {
                                                Intent i = new Intent(StoreListUsingSerarchActivrty.this, CheckOutStoreActivity.class);
                                                i.putExtra(CommonString.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));
                                                startActivity(i);
                                            } else {
                                                Snackbar.make(lv, R.string.nonetwork, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            ArrayList<CoverageBean> coverage_data = database.getCoverageSpecificData(jcplist.get(position).getStore_cd().get(0), jcplist.get(position).getVISIT_DATE().get(0));

            if (jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_u);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.checkinclose.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D))) {
                holder.img.setVisibility(View.INVISIBLE);
                holder.checkinclose.setBackgroundResource(R.drawable.tick_d);
                holder.checkinclose.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_P))) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_p);
                holder.checkinclose.setVisibility(View.INVISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (coverage_data.size() > 0 && coverage_data.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_C) ||
                    jcplist.get(position).getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_c);
                holder.checkinclose.setVisibility(View.INVISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (coverage_data.size() > 0) {
                if (coverage_data.get(0).getStatus().equals(CommonString.STORE_STATUS_LEAVE)) {
                    holder.img.setBackgroundResource(R.drawable.leave_tick);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.checkinclose.setVisibility(View.INVISIBLE);
                    holder.checkout.setVisibility(View.INVISIBLE);
                } else if (coverage_data.get(0).getStatus().equals(CommonString.KEY_CHECK_IN)) {
                    if (ValidateForCheckout(coverage_data.get(0).getStoreId(), coverage_data.get(0).getVisitDate())) {
                        holder.checkout.setBackgroundResource(R.drawable.checkout);
                        holder.checkout.setVisibility(View.VISIBLE);
                        holder.checkout.setEnabled(true);
                        holder.checkinclose.setVisibility(View.INVISIBLE);
                        holder.img.setVisibility(View.VISIBLE);
                        holder.img.setBackgroundResource(R.drawable.store);
                    } else {
                        holder.checkout.setEnabled(false);
                        holder.checkout.setVisibility(View.INVISIBLE);
                        holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
                        holder.checkinclose.setVisibility(View.VISIBLE);
                        holder.img.setVisibility(View.INVISIBLE);
                        holder.img.setBackgroundResource(R.drawable.store);
                    }
                }
            } else {
                holder.img.setVisibility(View.VISIBLE);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.checkinclose.setEnabled(false);
                holder.checkinclose.setVisibility(View.INVISIBLE);
            }

            holder.storename.setText(jcplist.get(position).getStore_name().get(0) + "(Store Id - " + jcplist.get(position).getStore_cd().get(0) + ")");
            holder.city.setText(jcplist.get(position).getCity().get(0));
            holder.keyaccount.setText(jcplist.get(position).getKey_account().get(0));
            return convertView;
        }

        private class ViewHolder {
            TextView storename, city, keyaccount;
            ImageView img, checkinclose;
            Button checkout;
        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        StoreListUsingSerarchActivrty.this.finish();
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        ArrayList<CoverageBean> coverage_data = database.getCoverageData(date);
        if (jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
            Snackbar.make(lv, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_P)) {
            Snackbar.make(lv, R.string.title_store_list_activity_store_again_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (jcplist.get(position).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)) {
            Snackbar.make(lv, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else if (jcplist.get(position).getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
            Snackbar.make(lv, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            boolean enteryflag = true;
            if (coverage_data.size() > 0) {
                for (int i2 = 0; i2 < coverage_data.size(); i2++) {
                    if (coverage_data.get(i2).getStoreId().equals(jcplist.get(position).getStore_cd().get(0))) {
                        if (coverage_data.get(i2).getStatus().equals(CommonString.STORE_STATUS_LEAVE)) {
                            Snackbar.make(lv, R.string.title_store_list_activity_already_store_closed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            enteryflag = false;
                            break;
                        }
                    }
                }

                if (enteryflag) {
                    for (int i = 0; i < coverage_data.size(); i++) {
                        if (coverage_data.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                            if (!coverage_data.get(i).getStoreId().equals(jcplist.get(position).getStore_cd().get(0))) {
                                Snackbar.make(lv, R.string.title_store_list_checkout_current, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                enteryflag = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (enteryflag) {
                showMyDialog(jcplist.get(position).getStore_cd().get(0), jcplist.get(position).getStore_name().get(0),
                        jcplist.get(position).getVISIT_DATE().get(0));
            }
        }
    }

    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }

    void showMyDialog(final String storeCd, final String storeName, final String visit_date) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    dialog.dismiss();
                    boolean flag = true;
                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_STORE_NAME, storeName);
                    editor.putString(CommonString.KEY_STORE_CD, storeCd);
                    editor.commit();

                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {
                            if (storeCd.equals(coverage.get(i).getStoreId())) {
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        Intent in = new Intent(StoreListUsingSerarchActivrty.this, StoreImageActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else {
                        Intent in = new Intent(StoreListUsingSerarchActivrty.this, StoreEntry.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                } else if (checkedId == R.id.no) {
                    dialog.dismiss();
                    ArrayList<CoverageBean> specificCDATA;
                    specificCDATA = database.getCoverageSpecificData(storeCd, visit_date);
                    if (specificCDATA.size() > 0 && specificCDATA.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListUsingSerarchActivrty.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                UpdateData(storeCd);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(CommonString.KEY_STORE_CD, storeCd);
                                                editor.putString(CommonString.KEY_STOREVISITED, "");
                                                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                editor.commit();
                                                Intent in = new Intent(StoreListUsingSerarchActivrty.this, NonWorkingReason.class);
                                                startActivity(in);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        UpdateData(storeCd);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_CD, storeCd);
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.commit();
                        Intent in = new Intent(StoreListUsingSerarchActivrty.this, NonWorkingReason.class);
                        startActivity(in);
                    }
                }
            }

        });

        dialog.show();
    }


    public void UpdateData(String storeCd) {
        database.open();
        database.deleteSpecificStoreData(storeCd);
        database.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0), "N");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            StoreListUsingSerarchActivrty.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ValidateConditionData() {
        if (jcplist.size() > 0 && coverage.size() > 0) {
            for (int k = 0; k < coverage.size(); k++) {
                if (coverage.get(k).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                    autocomp_txt.setText(coverage.get(k).getSearch_storeCODE());
                    search_store.setText(coverage.get(k).getSearch_storeNAME());
                    card_autoc.setVisibility(View.VISIBLE);
                    go.setVisibility(View.VISIBLE);
                    card_autoc.setEnabled(false);
                    search_store.setEnabled(false);
                    autocomp_txt.setEnabled(false);
                    go.setEnabled(false);
                    break;
                } else {
                    card_autoc.setVisibility(View.VISIBLE);
                    go.setEnabled(true);
                    card_autoc.setEnabled(true);
                    search_store.setEnabled(true);
                    autocomp_txt.setEnabled(true);
                    search_store.setText("");
                    autocomp_txt.setText("");
                }
            }

            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
            lv.setVisibility(View.VISIBLE);
            nodata_rl.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            nodata_linear.setVisibility(View.GONE);
            card_autoc.setVisibility(View.VISIBLE);
            nodata_rl.setVisibility(View.GONE);
        } else if (jcplist.size() > 0) {
            for (int i = 0; i < jcplist.size(); i++) {
                if (jcplist.get(i).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_P)
                        || jcplist.get(i).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D)
                        || jcplist.get(i).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {

                }
            }
            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
            lv.setVisibility(View.VISIBLE);
            nodata_rl.setVisibility(View.GONE);
            nodata_linear.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            card_autoc.setVisibility(View.VISIBLE);
            card_autoc.setEnabled(true);
            search_store.setEnabled(true);
            autocomp_txt.setEnabled(true);
            go.setEnabled(true);
            search_store.setText("");
            autocomp_txt.setText("");
        } else if (database.isJCPDataFilled(date)) {
            nodata_rl.setVisibility(View.GONE);
            card_autoc.setVisibility(View.GONE);
            nodata_linear.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        } else if (jcplist.size() == 0 && database.isNonWorkingReason()) {
            lv.setVisibility(View.GONE);
            nodata_linear.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            nodata_rl.setVisibility(View.VISIBLE);
            card_autoc.setVisibility(View.VISIBLE);
            go.setEnabled(true);
            card_autoc.setEnabled(true);
            search_store.setEnabled(true);
            autocomp_txt.setEnabled(true);
            search_store.setText("");
            autocomp_txt.setText("");
        } else {
            if (database.isNonWorkingReason()) {
                lv.setVisibility(View.GONE);
                nodata_rl.setVisibility(View.VISIBLE);
                card_autoc.setVisibility(View.VISIBLE);
                nodata_linear.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else {
                nodata_rl.setVisibility(View.GONE);
                card_autoc.setVisibility(View.GONE);
                nodata_linear.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        }
    }


    private class BackgroundTask extends AsyncTask<Void, String, String> {
        private Context context;
        private String legecy_code;
        private String lagecy_storeN;
        private boolean flag = true;

        BackgroundTask(Context context, String legecy_code, String lagecy_storeN) {
            this.legecy_code = legecy_code;
            this.lagecy_storeN = lagecy_storeN;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Downloading Data", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            XmlPullParserFactory factory = null;
            try {
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                String onXML = "[DATA][USER_DATA]"
                        + "[StoreName]"
                        + lagecy_storeN
                        + "[/StoreName]"
                        + "[storecode]"
                        + legecy_code
                        + "[/storecode]"
                        + "[userId]"
                        + username
                        + "[/userId]"
                        + "[/USER_DATA]"
                        + "[/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_SEARCH_LIST_STORE);
                request.addProperty("onXML", onXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.NAMESPACE + CommonString.METHOD_SEARCH_LIST_STORE, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString() != null) {
                    if (result.toString().equalsIgnoreCase("False")) {
                        return "NO JOURNEY PLAN DATA FOUND";
                    }

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    jcpData = XMLHandlers.JCPXMLHandler(xpp, eventType);
                    database.jcpjourneyPlan(jcpData.getTable_journey_plan());
                }


                if (jcpData.getStore_cd().size() == 0) {
                    return "NO JOURNEY PLAN DATA FOUND";
                }


                database.open();
                database.insertJCPTEMPData(jcpData);
                return CommonString.KEY_SUCCESS;

            } catch (final IOException e) {
                loading.dismiss();
                flag = false;

            } catch (final Exception e) {
                loading.dismiss();
                Crashlytics.logException(e);
                flag = false;
            }

            return "";
        }

        @Override
        protected void onPostExecute(final String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            loading.dismiss();
            if (flag) {
                if (result.equals(CommonString.KEY_SUCCESS)) {
                    database.open();
                    ArrayList<JourneyPlanGetterSetter> tempStorelistData = new ArrayList<>();
                    tempStorelistData = database.getJCPTEMPData(date);
                    Toast.makeText(StoreListUsingSerarchActivrty.this, AlertMessage.MESSAGE_DOWNLOAD, Toast.LENGTH_LONG).show();
                    searchStorePopup(StoreListUsingSerarchActivrty.this, tempStorelistData);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    result.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                AlertMessage.MESSAGE_SOCKETEXCEPTION + " (" + result.toString() + ")", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public void searchStorePopup(final StoreListUsingSerarchActivrty context, final ArrayList<JourneyPlanGetterSetter> listItems) {
        final Dialog dialogsearchp = new Dialog(context);
        dialogsearchp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogsearchp.setContentView(R.layout.search_store_popup);
        dialog_list = (ListView) dialogsearchp.findViewById(R.id.dialog_list);
        dialogsearchp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialogsearchp.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogsearchp.setCanceledOnTouchOutside(true);
        dialogsearchp.show();

        DialogMyAdapter adapter = new DialogMyAdapter(context, listItems);
        dialog_list.setAdapter(adapter);
        listItems.size();
        dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<JourneyPlanGetterSetter> jcpGetterSetterArrayList;
                ArrayList<JourneyPlanGetterSetter> newlist;
                database.open();
                newlist = database.getJCPTEMPSPECIFICData(listItems.get(position).getStore_cd().get(0));
                jcpGetterSetterArrayList = database.getJCPData(date);
                if (jcpGetterSetterArrayList.size() > 0) {
                    for (int k = 0; k < jcpGetterSetterArrayList.size(); k++) {
                        if (listItems.get(position).getStore_cd().get(0).equals(jcpGetterSetterArrayList.get(k).getStore_cd().get(0))) {
                            Toast.makeText(StoreListUsingSerarchActivrty.this,
                                    "This Store Already Checked " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                            boolean flag = true;
                            if (newlist.get(0).getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
                                flag = false;
                                Toast.makeText(StoreListUsingSerarchActivrty.this, "This Store Already Checkout  " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                                break;
                            } else if (newlist.get(0).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D) ||
                                    newlist.get(0).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                                flag = false;
                                Toast.makeText(StoreListUsingSerarchActivrty.this, "This Store Already Uploaded " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                                break;
                            } else {
                                if (flag)
                                    dialogsearchp.dismiss();
                                showMyDialogFortemporary(newlist);
                            }
                        }
                    }
                } else {
                    boolean flag_no = true;
                    if (newlist.get(0).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_D) ||
                            newlist.get(0).getUploadStatus().get(0).equalsIgnoreCase(CommonString.KEY_U)) {
                        flag_no = false;
                        Toast.makeText(StoreListUsingSerarchActivrty.this, "This Store Already Uploaded " + "Please Visit Another Store", Toast.LENGTH_LONG).show();

                    } else if (newlist.get(0).getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
                        flag_no = false;
                        Toast.makeText(StoreListUsingSerarchActivrty.this, "This Store Already Checkout  " + "Please Visit Another Store", Toast.LENGTH_LONG).show();
                    } else {
                        if (flag_no)
                            dialogsearchp.dismiss();
                        showMyDialogFortemporary(newlist);

                    }
                }
            }
        });
    }

    class DialogMyAdapter extends BaseAdapter {
        Context context;
        ArrayList<JourneyPlanGetterSetter> parentlist;

        DialogMyAdapter(Context context, ArrayList<JourneyPlanGetterSetter> list_parent) {
            this.context = context;
            this.parentlist = list_parent;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return parentlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolderfotTemp holder = null;
            if (convertView == null) {
                holder = new ViewHolderfotTemp();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.dialogstorelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.tvstorename_);
                holder.city = (TextView) convertView.findViewById(R.id.tvcity_);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.tvkeyaccount_);
                holder.img = (ImageView) convertView.findViewById(R.id.img_);
                holder.storenamelistview_layout = (RelativeLayout) convertView.findViewById(R.id.storenamelistview_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderfotTemp) convertView.getTag();
            }

            holder.storename.setText(parentlist.get(position).getStore_name().get(0));
            holder.city.setText(parentlist.get(position).getCity().get(0));
            holder.keyaccount.setText(parentlist.get(position).getKey_account().get(0));
            holder.img.setVisibility(View.VISIBLE);
            holder.img.setBackgroundResource(R.drawable.store);
            return convertView;
        }

        class ViewHolderfotTemp {
            TextView storename, city, keyaccount;
            ImageView img;
            RelativeLayout storenamelistview_layout;
        }
    }

    void showMyDialogFortemporary(final ArrayList<JourneyPlanGetterSetter> object) {
        final Dialog dialogfortemp = new Dialog(this);
        dialogfortemp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogfortemp.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialogfortemp.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    dialogfortemp.cancel();
                    dialogfortemp.dismiss();
                    database.open();
                    database.insertJCPSELECTEDData(object);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, object.get(0).getStore_cd().get(0));
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_STORE_NAME, object.get(0).getStore_name().get(0));
                    editor.putString(CommonString.KEY_EMP_CD, object.get(0).getEmp_cd().get(0));
                    editor.putString(CommonString.KEY_SEARCH_STORE_CODE, autocomp_txt.getText().toString());
                    editor.putString(CommonString.KEY_SEARCH_STORE_NAME, search_store.getText().toString());
                    editor.commit();
                    Intent in = new Intent(StoreListUsingSerarchActivrty.this, StoreImageActivity.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                    //  StoreListUsingSerarchActivrty.this.finish();

                } else if (checkedId == R.id.no) {
                    dialogfortemp.dismiss();
                    dialogfortemp.cancel();
                    database.open();
                    database.insertJCPSELECTEDData(object);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_CD, object.get(0).getStore_cd().get(0));
                    editor.putString(CommonString.KEY_STORE_NAME, object.get(0).getStore_name().get(0));
                    editor.putString(CommonString.KEY_EMP_CD, object.get(0).getEmp_cd().get(0));
                    editor.putString(CommonString.KEY_SEARCH_STORE_CODE, autocomp_txt.getText().toString());
                    editor.putString(CommonString.KEY_SEARCH_STORE_NAME, search_store.getText().toString());
                    editor.commit();
                    Intent in = new Intent(StoreListUsingSerarchActivrty.this, NonWorkingReason.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                }
            }
        });

        dialogfortemp.show();
    }


    private void validateSearchOption() {
        autocomp_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    search_store.setEnabled(false);
                } else {
                    search_store.setEnabled(true);
                    autocomp_txt.setEnabled(true);
                }
            }
        });
        search_store.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    autocomp_txt.setEnabled(false);
                } else {
                    search_store.setEnabled(true);
                    autocomp_txt.setEnabled(true);
                }
            }
        });
    }

    boolean ValidateForCheckout(String store_cd, String visit_date) {
        boolean status = true;
        if (!database.isStoreAuditFilled(store_cd, visit_date)) {
            status = false;
        }
        return status;
    }

}
