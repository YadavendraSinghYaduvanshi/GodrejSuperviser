package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.godrejsupervisor.R;
import com.cpm.database.GODREJDatabase;
import com.cpm.xmlGetterSetter.AuditQuestion;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class StoreAuditActivity extends AppCompatActivity {
    GODREJDatabase db;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, visit_date, user_type, username, Error_Message;
    ExpandableListView lvExp_audit;
    FloatingActionButton storeAudit_fab;
    List<AuditQuestion> listDataHeader;
    List<AuditQuestion> questionList = new ArrayList<>();
    HashMap<AuditQuestion, List<AuditQuestion>> listDataChild;
    ExpandableListAdapter listAdapter;
    static int grp_position = -1, child_position = -1;
    String _pathforcheck, _path, img1 = "",store_name;
    boolean checkflag = true;
    ArrayList<Integer> checkHeaderArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_audit);
        auditUI();
        //save audit data
        storeAudit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvExp_audit.clearFocus();
                lvExp_audit.invalidateViews();
                listAdapter.notifyDataSetChanged();
                if (validateData(listDataChild, listDataHeader)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreAuditActivity.this);
                    builder.setTitle(getString(R.string.parinaam)).setMessage(R.string.alertsaveData);
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.open();
                            db.insertStoreAuditData(store_cd, visit_date, listDataChild, listDataHeader);
                            finish();
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Snackbar.make(lvExp_audit, Error_Message, Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }

    private void auditUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvExp_audit = (ExpandableListView) findViewById(R.id.lvExp_audit);
        storeAudit_fab = (FloatingActionButton) findViewById(R.id.storeAudit_fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString.KEY_STORE_CD, "");
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, "");
        user_type = preferences.getString(CommonString.KEY_USER_TYPE, "");
        store_name = preferences.getString(CommonString.KEY_STORE_NAME, "");
        getSupportActionBar().setTitle("Store Audit -" + visit_date);
        db = new GODREJDatabase(this);
        db.open();
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting brandMasterArrayList adapter
        lvExp_audit.setAdapter(listAdapter);

        lvExp_audit.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                lvExp_audit.clearFocus();
                if (SCROLL_STATE_TOUCH_SCROLL == arg1) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }
        });
    }


    private void prepareListData() {
        db.open();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader = db.getStoreAuditHeaderData();
        if (listDataHeader.size() > 0) {
            for (int i = 0; i < listDataHeader.size(); i++) {
                questionList = db.getStoreAuditInsertedData(store_cd, listDataHeader.get(i).getQUESTION_CATEGORY_ID().get(0), visit_date);
                if (questionList.size() > 0) {
                    storeAudit_fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.edit_txt));
                } else {
                    questionList = db.getStoreAuditChildData(listDataHeader.get(i).getQUESTION_CATEGORY_ID().get(0));
                }
                listDataChild.put(listDataHeader.get(i), questionList); // Header, Child data
            }
        }
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<AuditQuestion> _listDataHeader;
        private HashMap<AuditQuestion, List<AuditQuestion>> _listDataChild;

        public ExpandableListAdapter(Context context, List<AuditQuestion> listDataHeader,
                                     HashMap<AuditQuestion, List<AuditQuestion>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            final AuditQuestion childText = (AuditQuestion) getChild(groupPosition, childPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item_parent_storeaudit, null);
                holder = new ViewHolder();
                holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
                holder.audit_spin = (Spinner) convertView.findViewById(R.id.audit_spin);
                holder.audit_came = (ImageView) convertView.findViewById(R.id.audit_came);
                holder.audit_remark = (EditText) convertView.findViewById(R.id.audit_remark);
                holder.img_rl = (LinearLayout) convertView.findViewById(R.id.img_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

            txtListChild.setText(childText.getQUESTION().get(0));

            holder.audit_came.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lvExp_audit.clearFocus();
                    grp_position = groupPosition;
                    child_position = childPosition;
                    _pathforcheck = store_cd + "_" +
                            childText.getQUESTION_ID().get(0) + "_AUDITIMG_" + visit_date.replace("/", "") + "_"
                            + getCurrentTime().replace(":", "") + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(_context, _path);
                }
            });


            //for reason spinner
            final ArrayList<AuditQuestion> reason_list = db.getauditAnswerData(childText.getQUESTION_ID().get(0));
            AuditQuestion non = new AuditQuestion();
            non.setANSWER("-Select Answer-");
            non.setANSWER_ID("0");
            reason_list.add(0, non);
            holder.audit_spin.setAdapter(new ReasonSpinnerAdapter(_context, R.layout.spinner_text_view, reason_list));

            for (int i = 0; i < reason_list.size(); i++) {
                if (reason_list.get(i).getANSWER_ID().get(0).equals(_listDataChild.get(listDataHeader.get(groupPosition))
                        .get(childPosition).getCurrectanswer_cd())) {
                    holder.audit_spin.setSelection(i);
                    break;
                }
            }

            holder.audit_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos != 0) {
                        AuditQuestion ans = reason_list.get(pos);
                        _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setCurrectanswer_cd(ans.getANSWER_ID().get(0).toString());
                        _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setCurrectSPINAnwer(ans.getANSWER().get(0).toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            ////////changessssssssssssssssssssssssss
            if (!_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getCAMERA_ALLOW().get(0).equals("0")) {
                holder.img_rl.setVisibility(View.VISIBLE);
                holder.img_rl.setId(childPosition);
            } else {
                holder.img_rl.setVisibility(View.GONE);
                holder.img_rl.setId(childPosition);
            }

            if (!img1.equals("")) {
                if (grp_position == groupPosition) {
                    if (child_position == childPosition) {
                        _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setAuditQuestion_cam(img1);
                        img1 = "";
                    }
                }
            }

            if (!_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAuditQuestion_cam().equals("")) {
                holder.audit_came.setImageResource(R.drawable.ic_menu_camera_done);
                holder.audit_came.setId(childPosition);
            } else {
                holder.audit_came.setImageResource(R.drawable.ic_menu_camera_black);
                holder.audit_came.setId(childPosition);
            }

            ///for remark............
            ////////changessssssssssssssssssssssssss
            if (!_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getREMARK_ALLOW().get(0).equals("0")) {
                holder.audit_remark.setVisibility(View.VISIBLE);
                holder.audit_remark.setId(childPosition);
            } else {
                holder.audit_remark.setVisibility(View.GONE);
                holder.audit_remark.setId(childPosition);
            }

            holder.audit_remark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final EditText Caption = (EditText) v;
                        int pos = Caption.getText().length();
                        Caption.setSelection(pos);
                        String value = Caption.getText().toString().replaceAll("[&+!^/?*#:<>{}'%$|]", " ");
                        if (value.equals("")) {
                            _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setAudit_remark("");
                        } else {
                            _listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setAudit_remark(value);
                        }

                        lvExp_audit.clearFocus();
                        lvExp_audit.invalidateViews();
                    }
                }
            });


            holder.audit_remark.setText(_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAudit_remark());
            holder.audit_remark.setId(childPosition);
            if (!checkflag) {
                boolean tempflag = false;
                if (_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getCAMERA_ALLOW().get(0).equals("1")) {
                    if (_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getAuditQuestion_cam().equals("")) {
                        tempflag = true;
                    }
                }
                if (_listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getCurrectanswer_cd().equals("0")) {
                    tempflag = true;
                }
                if (tempflag) {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                }
            } else {
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final AuditQuestion headerTitle = (AuditQuestion) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group_storeaudit, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);

            CardView card_view = (CardView) convertView.findViewById(R.id.card_view);

            lblListHeader.setText(headerTitle.getQUESTION_CATEGORY().get(0));
            if (!checkflag) {
                if (checkHeaderArray.contains(groupPosition)) {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            } else {
                card_view.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class ViewHolder {
        Spinner audit_spin;
        CardView cardView;
        ImageView audit_came;
        EditText audit_remark;
        LinearLayout img_rl;
    }


    boolean validateData(HashMap<AuditQuestion, List<AuditQuestion>> listDataChild2, List<AuditQuestion> listDataHeader2) {
        checkflag = true;
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader2.size(); i++) {
            for (int j = 0; j < listDataChild2.get(listDataHeader2.get(i)).size(); j++) {
                String spinValue = listDataChild2.get(listDataHeader2.get(i)).get(j).getCurrectanswer_cd();
                String question = listDataChild2.get(listDataHeader2.get(i)).get(j).getQUESTION().get(0);
                String cameraFlag = listDataChild2.get(listDataHeader2.get(i)).get(j).getCAMERA_ALLOW().get(0);
                String audit_img = listDataChild2.get(listDataHeader2.get(i)).get(j).getAuditQuestion_cam();
                if (cameraFlag.equals("1")) {
                    if (audit_img.equals("")) {
                        Error_Message = "Please Capture '" + question + "' Image.";
                        checkflag = false;
                        break;
                    }
                }
                if (checkflag && spinValue.equals("0")) {
                    Error_Message = "Please Select '" + question + "' Dropdown Answer";
                    checkflag = false;
                    break;
                } else {
                    checkflag = true;
                }
            }
            if (!checkflag) {
                if (!checkHeaderArray.contains(i)) {
                    checkHeaderArray.add(i);
                }
                break;
            }
        }
        listAdapter.notifyDataSetChanged();
        return checkflag;
    }


    public class ReasonSpinnerAdapter extends ArrayAdapter<AuditQuestion> {
        List<AuditQuestion> list;
        Context context;
        int resourceId;

        public ReasonSpinnerAdapter(Context context, int resourceId, ArrayList<AuditQuestion> list) {
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
            AuditQuestion cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(list.get(position).getANSWER().get(0));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(resourceId, parent, false);
            AuditQuestion cm = list.get(position);
            TextView txt_spinner = (TextView) view.findViewById(R.id.txt_sp_text);
            txt_spinner.setText(cm.getANSWER().get(0));

            return view;
        }

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

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
                            String metadata = CommonFunctions.setMetadataAtImages(store_name, store_cd, "Audit Image", username);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(StoreAuditActivity.this, _path, metadata, visit_date);
                            img1 = _pathforcheck;
                            lvExp_audit.invalidateViews();
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


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StoreAuditActivity.this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                        StoreAuditActivity.this.finish();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreAuditActivity.this);
            builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            StoreAuditActivity.this.finish();
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
