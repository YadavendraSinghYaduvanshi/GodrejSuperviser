package com.cpm.dailyentry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.cpm.GpsTracker.GPSTracker;
import com.cpm.PnGSupervisor.MainMenuActivity;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.capitalfoods.R;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DailyEntryScreen extends AppCompatActivity implements OnItemClickListener, LocationListener, ObservableScrollViewCallbacks {

    Button pjpdeviation, callcycle;
    GSKDatabase database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    ArrayList<JourneyPlanGetterSetter> jcplist1;
    ArrayList<JourneyPlanGetterSetter> jcplist2;
    private SharedPreferences preferences;
    private String date, store_intime;
    //ListView lv;
    ObservableListView lv;
    String REasonID, store_cd1, storeName;

    private SharedPreferences.Editor editor = null;

    private Dialog dialog;

    String storeVisited = null;

    public static String currLatitude = "0.0";
    public static String currLongitude = "0.0";
    String latitud, longitud;
    String user_type;
    ArrayList<CoverageBean> coverage;
    String str;
    CardView cardView;
    String datacheck = "";
    String[] words;
    String validity, storeCd;
    Object result;
    String store_cd, visit_date, username, intime, store_id, _UserId, app_ver;
    boolean exception = false;
    MalformedURLException e;
    boolean resultflag = false;

    LinearLayout parent_linear, nodata_linear;
    boolean result_flag = false, leaveflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistlayout);
        //lv=(ListView) findViewById(R.id.list);
        lv = (ObservableListView) findViewById(R.id.obserlist);


        nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
        parent_linear = (LinearLayout) findViewById(R.id.parent_linear);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = new GSKDatabase(this);
        database.open();


        preferences = PreferenceManager.getDefaultSharedPreferences(this);

		/*editor = preferences.edit();




		editor.putString(CommonString.KEY_STOREVISITED, storeCd);
		editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
		editor.putString(CommonString.KEY_LATITUDE, currLatitude);
		editor.putString(CommonString.KEY_LONGITUDE, currLongitude);

		editor.putString(CommonString.KEY_STORE_NAME, storeName);
		editor.putString(CommonString.KEY_STORE_CD, storeCd);

		editor.commit();
*/


        date = preferences.getString(CommonString.KEY_DATE, null);
        //REasonID = preferences.getString(CommonString.KEY_REASON_ID, null);
        store_cd1 = preferences.getString(CommonString.KEY_STORE_CD, null);


        store_intime = preferences.getString(CommonString.KEY_STORE_IN_TIME, "");

        store_cd = preferences.getString(CommonString.KEY_STORE_CD, null);

        username = preferences.getString(CommonString.KEY_USERNAME, null);


        app_ver = preferences.getString(CommonString.KEY_VERSION, "");

        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        _UserId = preferences.getString(CommonString.KEY_USER_ID, null);

        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        intime = getCurrentTime1();


        editor = preferences.edit();

        user_type = preferences.getString(CommonString.KEY_USER_TYPE, null);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv.setScrollViewCallbacks(this);


        GPSTracker gps = new GPSTracker(DailyEntryScreen.this);
        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        latitud = String.valueOf(latitude);
        longitud = String.valueOf(longitude);


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

/*
        jcplist1=database.getJCPData(date);
		for(int i=0;i<jcplist1.size();i++) {
			jcplist.add(jcplist1.get(0));
			jcplist.add(jcplist1.get(1));
		}*/

        if (jcplist.size() > 0) {

            //setCheckOutData();

            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
        } else {
            lv.setVisibility(View.GONE);
            parent_linear.setBackgroundColor((getResources().getColor(R.color.grey_light)));
            nodata_linear.setVisibility(View.VISIBLE);
        }


    }

//}

    public void setCheckOutData() {

        for (int i = 0; i < jcplist.size(); i++) {
            String storeCd = jcplist.get(i).getStore_cd().get(0);
            if (!jcplist.get(i).getCheckOutStatus().get(0)
                    .equals(CommonString.KEY_C) && !jcplist.get(i).getCheckOutStatus().get(0)
                    .equals(CommonString.KEY_VALID)) {

                if (database.isOpeningDataFilled(storeCd) && database.getFacingCompetitorData(storeCd).size() > 0) {

                    boolean flag = true;

                    if (database.getPromotionBrandData(storeCd).size() > 0) {

                        if (database.isPromotionDataFilled(storeCd)) {
                            flag = true;
                        } else {
                            flag = false;
                        }

                    }

                    if (flag) {

                        if (user_type.equals("Promoter")) {

                            if (database.isClosingDataFilled(storeCd) && database.isMiddayDataFilled(storeCd)) {

                                flag = true;

                            } else {
                                flag = false;
                            }

                        }

                    }


                    if (flag) {

                        if (database.getAssetBrandData(storeCd).size() > 0) {

                            if (database.isAssetDataFilled(storeCd)) {

                                flag = true;
                            } else {

                                flag = false;
                            }
                        }

                    }

                    if (flag) {
                        database.updateStoreStatusOnCheckout(storeCd, date, CommonString.KEY_VALID);
                        jcplist = database.getJCPData(date);
                    }

                }


            }

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
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storelistrow, null);

                holder.storename = (TextView) convertView
                        .findViewById(R.id.tvstorename);
                holder.city = (TextView) convertView
                        .findViewById(R.id.tvcity);
                holder.keyaccount = (TextView) convertView
                        .findViewById(R.id.tvkeyaccount);
                holder.img = (ImageView) convertView
                        .findViewById(R.id.img);

                holder.checkout = (Button) convertView
                        .findViewById(R.id.chkout);

                holder.checkinclose = (ImageView) convertView
                        .findViewById(R.id.closechkin);


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

                                                editor.putString(
                                                        CommonString.KEY_STORE_CD,
                                                        jcplist.get(position)
                                                                .getStore_cd().get(0));
                                                editor.putString(
                                                        CommonString.KEY_STORE_NAME,
                                                        jcplist.get(position)
                                                                .getStore_name().get(0));

                                                editor.commit();

                                                Intent i = new Intent(
                                                        DailyEntryScreen.this,
                                                        CheckOutStoreActivity.class);
                                                startActivity(i);
                                            } else {

                                                Snackbar.make(lv, "No Network", Snackbar.LENGTH_SHORT)
                                                        .setAction("Action", null).show();

                                                //Toast.makeText(DailyEntryScreen.this, "No Network", Toast.LENGTH_SHORT).show();

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
            ArrayList<CoverageBean> coverage = database.getCoverageSpecificData(storecd);

            if (jcplist.get(position).getUploadStatus().get(0).equals(CommonString.KEY_D)) {

                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_d);
                holder.img.setVisibility(View.VISIBLE);
                holder.checkinclose.setVisibility(View.GONE);

            } else if (coverage.size() == 0) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.store);
                holder.checkinclose.setVisibility(View.GONE);
                
            } else if (!coverage.get(0).getReasonid().equalsIgnoreCase("0")) {

                holder.img.setBackgroundResource(R.drawable.leave_tick);
                holder.img.setVisibility(View.VISIBLE);
                holder.checkinclose.setVisibility(View.GONE);


            } else if (coverage.get(0).getOutTime() != null) {
                holder.img.setBackgroundResource(R.drawable.tickgreenv);
                holder.checkinclose.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);

            } else {

                str = jcplist.get(position).getStore_name().get(0);
                holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
                holder.img.setBackgroundResource(R.drawable.checkin_ico);
                holder.img.setVisibility(View.VISIBLE);
                holder.checkinclose.setVisibility(View.GONE);
            }

            holder.checkout.setVisibility(View.GONE);

            //holder.checkinclose.setEnabled(false);
            //holder.checkinclose.setVisibility(View.VISIBLE);
            //	holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
            //}


            holder.storename.setText(jcplist.get(position).getStore_name().get(0));
            holder.city.setText(jcplist.get(position).getCity().get(0));
            holder.keyaccount.setText(jcplist.get(position).getKey_account().get(0));


			/*if (list.get(position).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                holder.imgtick.setVisibility(View.VISIBLE);
				holder.imgtick.setBackgroundResource(R.drawable.leave_tick);
			}*/

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
        Intent i = new Intent(this, MainMenuActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub

        if (CheckNetAvailability()) {


            store_cd = jcplist.get(position).getStore_cd().get(0);
            final String upload_status = jcplist.get(position).getUploadStatus().get(0);
            final String checkoutstatus = jcplist.get(position).getCheckOutStatus().get(0);
            final String storename = jcplist.get(position).getStore_name().get(0);
            //

            editor = preferences.edit();

            editor.putString(CommonString.KEY_STORE_CD, store_cd);
            editor.commit();


            if (upload_status.equals(CommonString.KEY_D)) {

                Snackbar.make(lv, "All Data Uploaded", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();


            } else if (leavesetflag(store_cd)) {
                Snackbar.make(lv, " Store Closed", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            } else if (!setcheckedmenthod(store_cd)) {

                boolean enteryflag = true;

                if (coverage.size() > 0) {

                    int i;

                    for (i = 0; i < coverage.size(); i++) {


                        if (coverage.get(i).getInTime() != null) {

                            if (coverage.get(i).getOutTime() == null) {
                                if (!store_cd.equals(coverage.get(i).getStoreId())) {

                                    Snackbar.make(lv, " Please fill out time of" + " " + str, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    enteryflag = false;
                                }
                                break;
                            }


                        }
			/*else
			{

			}*/

                    }


                }

                if (enteryflag) {
                    showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));


                }

            } else {

                Snackbar.make(lv, "Data already filled ", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

            //setcheckedmenthod(store_cd);
















			/*else if(!preferences.getString(CommonString.KEY_STOREVISITED, "").equals(store_cd)){

				Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
						.setAction("Action", null).show();

				//Toast.makeText(getApplicationContext(), "Please checkout from current store", Toast.LENGTH_SHORT).show();
			}
*/


		/*else if (((checkoutstatus.equals(CommonString.KEY_C)))) {

			Snackbar.make(lv, "Store already checked out", Snackbar.LENGTH_SHORT)
					.setAction("Action", null).show();

			//Toast.makeText(getApplicationContext(), "Store Checkout", Toast.LENGTH_SHORT).show();
		}*/

		/*else if(preferences.getString(CommonString.KEY_STOREVISITED_STATUS+store_cd, "").equals("No")){

			Snackbar.make(lv, "Store Already Closed", Snackbar.LENGTH_SHORT)
					.setAction("Action", null).show();

			//Toast.makeText(getApplicationContext(), "Store Already Closed", Toast.LENGTH_SHORT).show();
		}*/
            //else{
			/*if(jcplist.get(position).getCategory_type().get(0).equals("Food")){
				editor.putBoolean(CommonString.KEY_FOOD_STORE, true);
			}
			else{
				editor.putBoolean(CommonString.KEY_FOOD_STORE, false);
			}

			editor.commit();*/

			/*if(preferences.getString(CommonString.KEY_STOREVISITED_STATUS, "").equals("Yes")){


				if(!preferences.getString(CommonString.KEY_STOREVISITED, "").equals(store_cd)){

					Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT)
							.setAction("Action", null).show();

					//Toast.makeText(getApplicationContext(), "Please checkout from current store", Toast.LENGTH_SHORT).show();
				}
				else{

				*//*	editor.putString(CommonString.KEY_STORE_CD, store_cd+"");
					editor.putString(CommonString.KEY_STORE_NAME, jcplist.get(position).getStore_name().get(0));
					editor.commit();*//*

					showMyDialog(store_cd,jcplist.get(position).getStore_name().get(0),"No","",jcplist.get(position).getCheckOutStatus().get(0));

				}

			}*/
            //	else {

            // PUT IN PREFERENCES
				/*editor = preferences.edit();
				editor.putString(CommonString.KEY_STORE_CD, store_cd);
				editor.putString(CommonString.KEY_STORE_NAME, jcplist.get(position).getStore_name().get(0));
				editor.putString(CommonString.KEY_VISIT_DATE, jcplist.get(position).getVISIT_DATE().get(0));
				editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");

				editor.commit();*/

            //	showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));

            //	}

            //}


            //Toast.makeText(getApplicationContext(), store_name, Toast.LENGTH_SHORT).show();
		/*	Intent in=new Intent(getApplicationContext(),StoreEntry.class);

		startActivity(in);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);*/


        } else {
            Snackbar.make(lv, " No Network Available ", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }


    }


    public String getCurrentTime1() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());

      /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
               + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

        return cdate;

    }


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();
        int hour = m_cal.get(Calendar.HOUR_OF_DAY);
        int min = m_cal.get(Calendar.MINUTE);
        //int sec = m_cal.get(Calendar.MILLISECOND);
        int sec = m_cal.get(Calendar.SECOND);


        String intime = "";

        if (hour == 0) {
            intime = "" + 24 + ":" + min + ":" + sec;
        } else if (hour == 24) {
            intime = "" + 24 + ":" + min + ":" + sec;
        } else {

            if (hour > 24) {
                hour = hour - 24;
                intime = "" + hour + ":" + min + ":" + sec;
            } else {
                intime = "" + hour + ":" + min + ":" + sec;
            }
        }
        return intime;
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

    void showMyDialog(final String storeCd, final String storeName, final String status, final String visitDate, final String checkout_status) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        // dialog.setTitle("About Android Dialog Box");


        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
					/*Toast.makeText(getApplicationContext(), "choice: Yes", 
								Toast.LENGTH_SHORT).show();*/
                    editor = preferences.edit();

                    editor.putString(CommonString.KEY_STOREVISITED, store_cd);
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString.KEY_LATITUDE, currLatitude);
                    editor.putString(CommonString.KEY_LONGITUDE, currLongitude);

                    editor.putString(CommonString.KEY_STORE_NAME, storeName);
                    editor.putString(CommonString.KEY_STORE_CD, store_cd);

                    if (!visitDate.equals("")) {
                        editor.putString(CommonString.KEY_VISIT_DATE, visitDate);
                    }

                    if (status.equals("Yes")) {
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");
                    }

                    database.updateStoreStatusOnCheckout(storeCd, date, CommonString.KEY_INVALID);

                    editor.commit();

                    if (store_intime.equalsIgnoreCase("")) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_IN_TIME,
                                getCurrentTime());
                        //editor.putString(CommonString.KEY_STOREVISITED, store_id);
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "Yes");

                        editor.commit();

                    }

                    dialog.cancel();

                    //	Intent in  = new Intent(DailyEntryScreen.this, StoreEntry.class);


					/*if(coverage.size()==0) {


								new UploadingTask().execute();
					}


					else
					{*/
                    boolean flag = true;

                    if (coverage.size() > 0) {


                        for (int i = 0; i < coverage.size(); i++) {


                            if (store_cd.equals(coverage.get(i).getStoreId())) {

                                flag = false;
                                break;
                            }


                        }


                    }


                    if (flag == true) {
                        new UploadingTask().execute();
                    } else {

                        Intent in = new Intent(DailyEntryScreen.this, AttendenceActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    }


                    //}


                    //}


                    //finish();

                } else if (checkedId == R.id.no) {
					/*Toast.makeText(getApplicationContext(), "choice: No", 
								Toast.LENGTH_SHORT).show();*/


                    dialog.cancel();

                    if (checkout_status.equals(CommonString.KEY_INVALID) || checkout_status.equals(CommonString.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
                        builder.setMessage(CommonString.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {

                                                UpdateData(storeCd);

                                                SharedPreferences.Editor editor = preferences
                                                        .edit();
                                                editor.putString(CommonString.KEY_STORE_CD, storeCd);
                                                editor.putString(
                                                        CommonString.KEY_STORE_IN_TIME,
                                                        "");
                                                editor.putString(
                                                        CommonString.KEY_STOREVISITED,
                                                        "");
                                                editor.putString(
                                                        CommonString.KEY_STOREVISITED_STATUS,
                                                        "");
                                                editor.putString(
                                                        CommonString.KEY_LATITUDE, "");
                                                editor.putString(
                                                        CommonString.KEY_LONGITUDE, "");

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

                        SharedPreferences.Editor editor = preferences
                                .edit();
                        editor.putString(CommonString.KEY_STORE_CD, storeCd);
                        editor.putString(
                                CommonString.KEY_STORE_IN_TIME,
                                "");
                        editor.putString(
                                CommonString.KEY_STOREVISITED,
                                "");
                        editor.putString(
                                CommonString.KEY_STOREVISITED_STATUS,
                                "");
                        editor.putString(
                                CommonString.KEY_LATITUDE, "");
                        editor.putString(
                                CommonString.KEY_LONGITUDE, "");

                        editor.commit();

                        Intent in = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
                        startActivity(in);
                    }


                    //finish();
                }
            }

        });

		/*RadioButton yes = (RadioButton)  dialog.findViewById(R.id.yes);
		RadioButton no = (RadioButton)  dialog.findViewById(R.id.no);*/

        dialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        currLatitude = Double.toString(location.getLatitude());
        currLongitude = Double.toString(location.getLongitude());
    }


    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    public void UpdateData(String storeCd) {

        database.open();
        database.deleteSpecificStoreData(storeCd);


		/*database.updateStoreStatusOnLeave(store_cd, visit_date,
				CommonString.KEY_N);*/

        database.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0),
                "N");

		/*Intent in  = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
		startActivity(in);*/

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

            // NavUtils.navigateUpFromSameTask(this);
            finish();

            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean setcheckedmenthod(String store_cd) {


        for (int i = 0; i < coverage.size(); i++) {


            if (store_cd.equals(coverage.get(i).getStoreId())) {

                if (coverage.get(i).getOutTime() != null) {
                    result_flag = true;

                    break;
                }


                //	Snackbar.make(lv, " Data Filled", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                //

            } else {
                //	showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));

                result_flag = false;

                //	break;
            }


        }


        return result_flag;
    }


    public boolean leavesetflag(String store_cd) {


        for (int i = 0; i < coverage.size(); i++) {


            if (store_cd.equals(coverage.get(i).getStoreId())) {

                if (!coverage.get(i).getReasonid().equalsIgnoreCase("0")) {


                    leaveflag = true;

                    break;
                }

            }
            //	Snackbar.make(lv, " Data Filled", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            //


            else {
                //	showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes", jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0));

                leaveflag = false;

                //	break;
            }


        }


        return leaveflag;
    }


    private class UploadingTask extends AsyncTask<Void, String, String> {

        //	public AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);

        final ProgressDialog ringProgressDialog = ProgressDialog.show(DailyEntryScreen.this, "Please wait ...", "Uploading Data ...", true);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DailyEntryScreen.this);


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            //ringProgressDialog.show();
            ringProgressDialog.setCancelable(true);


        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                String onXML = "[DATA][USER_DATA][STORE_CD]"
                        + store_cd
                        + "[/STORE_CD]" + "[VISIT_DATE]"
                        + visit_date
                        + "[/VISIT_DATE][LATITUDE]"
                        + latitud
                        + "[/LATITUDE][APP_VERSION]"
                        + app_ver
                        + "[/APP_VERSION][LONGITUDE]"
                        + longitud
                        + "[/LONGITUDE][IN_TIME]"
                        + intime
                        + "[/IN_TIME][OUT_TIME]"
                        + "00:00:00"
                        + "[/OUT_TIME][UPLOAD_STATUS]"
                        + "N"
                        + "[/UPLOAD_STATUS][USER_ID]"
                        + username
                        + "[/USER_ID]" +
                        "[IMAGE_URL]"
                        + ""
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


                androidHttpTransport.call(CommonString.SOAP_ACTION + CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);


                result = (Object) envelope.getResponse();


                if (result.toString().contains("Success")) {
                    resultflag = true;


                }


                if (result.toString().contains(
                        CommonString.KEY_FALSE)) {
                    return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                }
                if (result.toString().contains(
                        CommonString.KEY_FAILURE)) {
                    return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                }


            } catch (MalformedURLException e) {

                exception = true;


            } catch (IOException e) {


                exception = true;


            } catch (Exception e) {


                exception = true;
            }


            return null;

        }

        @Override
        protected void onPostExecute(final String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            new Thread(new Runnable() {

                @Override

                public void run() {

                    try {


                        // Here you should write your time consuming task...

                        // Let the progress ring for 10 seconds...

                        Thread.sleep(5000);

                    } catch (Exception e) {


                    }

                    ringProgressDialog.dismiss();

                   /* Intent in  = new Intent(AttendenceActivity.this, StoreEntry.class);
                    startActivity(in);*/

                    if (resultflag == true) {

                        Intent in = new Intent(DailyEntryScreen.this, AttendenceActivity.class);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }


                }

            }).start();


            if (exception == true) {

                ringProgressDialog.dismiss();
                // Setting Dialog Title
                alertDialog.setTitle("Network Error");

                // Setting Dialog Message
                alertDialog.setMessage("Click ok to enable Internet");

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(
                                        Settings.ACTION_SETTINGS);
                                startActivity(intent);
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

    }


}
