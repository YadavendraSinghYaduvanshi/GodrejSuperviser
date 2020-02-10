package com.cpm.dailyentry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cpm.Constants.CommonString;

import com.cpm.database.GODREJDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.godrejsupervisor.R;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class DailyEntryScreen extends AppCompatActivity implements OnItemClickListener, ObservableScrollViewCallbacks {
    GODREJDatabase database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    private SharedPreferences preferences;
    private String date;
    ObservableListView lv;
    private SharedPreferences.Editor editor = null;
    private Dialog dialog;
    String user_type;
    ArrayList<CoverageBean> coverage;
    String visit_date, username, _UserId, app_ver;
    LinearLayout parent_linear, nodata_linear;
    boolean result_flag = false, leaveflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistlayout);
        lv = (ObservableListView) findViewById(R.id.obserlist);
        nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
        parent_linear = (LinearLayout) findViewById(R.id.parent_linear);
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
        lv.setScrollViewCallbacks(this);
    }

    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    public void onDownMotionEvent() {
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
        if (jcplist.size() > 0) {
            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
        } else {
            lv.setVisibility(View.GONE);
            parent_linear.setBackgroundColor((getResources().getColor(R.color.grey_light)));
            nodata_linear.setVisibility(View.VISIBLE);
        }
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            DailyEntryScreen.this);
                    builder.setMessage("Are you sure you want to Checkout")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            if (CheckNetAvailability()) {
                                                editor = preferences.edit();
                                                editor.putString(CommonString.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));
                                                editor.putString(CommonString.KEY_STORE_NAME, jcplist.get(position).getStore_name().get(0));
                                                editor.commit();
                                                Intent i = new Intent(DailyEntryScreen.this, CheckOutStoreActivity.class);
                                                startActivity(i);
                                            } else {
                                                Snackbar.make(lv, "No Network", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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

            String storecd = jcplist.get(position).getStore_cd().get(0);
            ArrayList<CoverageBean> coverage_data = database.getCoverageSpecificData(storecd, jcplist.get(position).getVISIT_DATE().get(0));

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
            } else if (jcplist.get(position).getCheckOutStatus().get(0).equalsIgnoreCase(CommonString.KEY_C)) {
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
                } else if (coverage_data.get(0).getStatus().equals(CommonString.KEY_VALID)) {
                    holder.checkout.setBackgroundResource(R.drawable.checkout);
                    holder.checkout.setVisibility(View.VISIBLE);
                    holder.checkout.setEnabled(true);
                    holder.checkinclose.setVisibility(View.INVISIBLE);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setBackgroundResource(R.drawable.store);
                } else if (coverage_data.get(0).getStatus().equals(CommonString.KEY_CHECK_IN)) {
                    holder.checkout.setEnabled(false);
                    holder.checkout.setVisibility(View.INVISIBLE);
                    holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
                    holder.checkinclose.setVisibility(View.VISIBLE);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.img.setBackgroundResource(R.drawable.store);
                }
            } else {
                holder.img.setVisibility(View.VISIBLE);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.checkinclose.setEnabled(false);
                holder.checkinclose.setVisibility(View.INVISIBLE);
            }

            holder.storename.setText(jcplist.get(position).getStore_name().get(0));
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
        DailyEntryScreen.this.finish();
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
                        if (coverage_data.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN) ||
                                coverage_data.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_INVALID)) {
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

    public String getCurrentTime1() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
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

    void showMyDialog(final String storeCd, final String storeName,final String visit_date) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    boolean flag = true;
                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_STORE_NAME, storeName);
                    editor.putString(CommonString.KEY_STORE_CD, storeCd);
                    editor.commit();
                    dialog.cancel();
                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {
                            if (storeCd.equals(coverage.get(i).getStoreId())) {
                                flag = false;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        Intent in = new Intent(DailyEntryScreen.this, StoreImageActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else {
                        Intent in = new Intent(DailyEntryScreen.this, StoreEntry.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }
                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    ArrayList<CoverageBean> specificCDATA;
                    specificCDATA = database.getCoverageSpecificData(storeCd,visit_date);
                    if (specificCDATA.size() > 0 && specificCDATA.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)
                            || specificCDATA.size() > 0 && specificCDATA.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
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
                                                Intent in = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
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
                        Intent in = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

}
