package com.cpm.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.Constants.CommonString;
import com.cpm.GetterSetter.StoreBean;
import com.cpm.GetterSetter.VisitorDetailGetterSetter;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.TableBean;
import com.cpm.xmlGetterSetter.AssetInsertdataGetterSetter;
import com.cpm.xmlGetterSetter.AssetMasterGetterSetter;
import com.cpm.xmlGetterSetter.AuditQuestion;
import com.cpm.xmlGetterSetter.CallsGetterSetter;
import com.cpm.xmlGetterSetter.CategoryMasterGetterSetter;
import com.cpm.xmlGetterSetter.ClosingStockInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.ColdroomClosingGetterSetter;
import com.cpm.xmlGetterSetter.CompanyGetterSetter;
import com.cpm.xmlGetterSetter.CompetitionPromotionGetterSetter;
import com.cpm.xmlGetterSetter.DeepFreezerGetterSetter;
import com.cpm.xmlGetterSetter.DeepFreezerTypeGetterSetter;
import com.cpm.xmlGetterSetter.FacingCompetionCompanyGetterSetter;
import com.cpm.xmlGetterSetter.FacingCompetitorGetterSetter;
import com.cpm.xmlGetterSetter.FeedbackGetterSetter;
import com.cpm.xmlGetterSetter.FoodStoreInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.HeaderGetterSetter;
import com.cpm.xmlGetterSetter.JCPGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.MappingAssetGetterSetter;
import com.cpm.xmlGetterSetter.MappingAvailabilityGetterSetter;
import com.cpm.xmlGetterSetter.MappingPromotionGetterSetter;
import com.cpm.xmlGetterSetter.MiddayStockInsertData;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;
import com.cpm.xmlGetterSetter.POIGetterSetter;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlGetterSetter.PromotionInsertDataGetterSetter;
import com.cpm.xmlGetterSetter.SkuMasterGetterSetter;
import com.cpm.xmlGetterSetter.StockGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.internal.service.Common;

@SuppressLint("LongLogTag")
public class GODREJDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "GODREJSUP_DATABASE";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    public GODREJDatabase(Context completeDownloadActivity) {
        super(completeDownloadActivity, DATABASE_NAME, null, DATABASE_VERSION);
    }// TODO Auto-generated constructor stub }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void close() {
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // CREATING TABLE FOR GTGSK
        db.execSQL(TableBean.getjcptable());
        db.execSQL(TableBean.getNonworkingtable());
        db.execSQL(TableBean.getAuditquestionTable());
        db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
        db.execSQL(CommonString.CREATE_TABLE_AUDIT_OPENINGHEADER_DATA);
        db.execSQL(CommonString.CREATE_TABLE_STORE_AUDIT_DATA);
        db.execSQL(CommonString.CREATE_TABLE_VISITOR_LOGIN);
        db.execSQL(CommonString.CREATE_TABLE_FEEDBACK);
        db.execSQL(CommonString.CREATE_feedback_save);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }


    public void deleteSpecificStoreData(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_AUDIT_OPENINGHEADER_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_AUDIT_DATA, CommonString.KEY_STORE_CD + "='" + storeid + "'", null);
    }


    public void deleteAllTables() {
        db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
        db.delete(CommonString.TABLE_INSERT_AUDIT_OPENINGHEADER_DATA, null, null);
        db.delete(CommonString.TABLE_STORE_AUDIT_DATA, null, null);
    }

    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_AUDIT_OPENINGHEADER_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_AUDIT_DATA, null, null);
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //JCP data
    public long insertJCPData(JourneyPlanGetterSetter data) {
        db.delete("JOURNEY_PLAN", null, null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            for (int i = 0; i < data.getStore_cd().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getStore_cd().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("KEYACCOUNT", data.getKey_account().get(i));
                values.put("STORENAME", data.getStore_name().get(i));
                values.put("CITY", data.getCity().get(i));
                values.put("STORETYPE", data.getStore_type().get(i));
                values.put("UPLOAD_STATUS", data.getUploadStatus().get(i));
                values.put("CHECKOUT_STATUS", data.getCheckOutStatus().get(i));
                l = db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert JCP Data ",
                    ex.toString());
        }
        return l;

    }

    public long insertAuditQuestionMasterData(AuditQuestion data) {
        db.delete("QUESTIONNAIRE", null, null);
        ContentValues values = new ContentValues();
        long l = 0;

        try {

            for (int i = 0; i < data.getQUESTION_CATEGORY_ID().size(); i++) {
                values.put("QUESTION_CATEGORY_ID", Integer.parseInt(data.getQUESTION_CATEGORY_ID().get(i)));
                values.put("QUESTION_CATEGORY", data.getQUESTION_CATEGORY().get(i));
                values.put("QUESTION", data.getQUESTION().get(i));
                values.put("QUESTION_ID", Integer.parseInt(data.getQUESTION_ID().get(i)));
                values.put("ANSWER", data.getANSWER().get(i));
                values.put("CAMERA_ALLOW", data.getCAMERA_ALLOW().get(i));
                values.put("ANSWER_ID", Integer.parseInt(data.getANSWER_ID().get(i)));
                values.put("REMARK_ALLOW", data.getREMARK_ALLOW().get(i));

                l = db.insert("QUESTIONNAIRE", null, values);

            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception while Insert Non Working Data ", ex.toString());
        }
        return l;

    }


    //Non Working data
    public void insertNonWorkingReasonData(NonWorkingReasonGetterSetter data) {
        db.delete("NON_WORKING_REASON", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getReason_cd().size(); i++) {
                values.put("REASON_CD", Integer.parseInt(data.getReason_cd().get(i)));
                values.put("REASON", data.getReason().get(i));
                values.put("ENTRY_ALLOW", data.getEntry_allow().get(i));
                db.insert("NON_WORKING_REASON", null, values);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception while Insert Non Working Data ", ex.toString());
        }
    }


    //get JCP Data

    public ArrayList<JourneyPlanGetterSetter> getJCPData(String date) {
        Log.d("FetchingStoredata--------------->Start<------------", "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN where VISIT_DATE = '" + date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME"))));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY")));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!", e.toString());
            Crashlytics.logException(e);
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }

    //get JCP Data

    public ArrayList<JourneyPlanGetterSetter> getAllJCPData() {

        Log.d("FetchingStoredata--------------->Start<------------",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from JOURNEY_PLAN where "+CommonString.KEY_CHECKOUT_STATUS+" = '"+CommonString.KEY_C+"'"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name((dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME"))));
                    sb.setCity((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY"))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS"))));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setVISIT_DATE((dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE"))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching JCP!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data---------------------->Stop<-----------",
                "-------------------");
        return list;

    }


    public ArrayList<NonWorkingReasonGetterSetter> getNonWorkingDataByFlag(boolean flag) {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<NonWorkingReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (flag) {
                        NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                        String name = dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW"));
                        if (name.equals("1")) {
                            sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                            sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                            sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                            list.add(sb);
                        }
                    } else {
                        NonWorkingReasonGetterSetter sb = new NonWorkingReasonGetterSetter();
                        sb.setReason_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_CD")));
                        sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON")));
                        sb.setEntry_allow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ENTRY_ALLOW")));
                        list.add(sb);
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Non working!!!!!!!!!!!",
                    e.toString());
            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }





    public long InsertCoverageData(CoverageBean data) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_ID, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_IN_TIME, data.getInTime());
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_IMAGE02, data.getImage02());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());

            values.put(CommonString.KEY_SEARCH_STORE_NAME, data.getSearch_storeNAME());
            values.put(CommonString.KEY_SEARCH_STORE_CODE, data.getSearch_storeCODE());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception while Insert Closes Data ",
                    ex.toString());
        }
        return l;
    }

    // getCoverageData
    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setSearch_storeCODE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_CODE)));
                    sb.setSearch_storeNAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());

        }

        return list;

    }

    public ArrayList<CoverageBean> getCoverageDataReason(String visitdate, String store_cd) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' AND " + CommonString.KEY_STORE_ID + "='" + store_cd + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setSearch_storeCODE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_CODE)));
                    sb.setSearch_storeNAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public ArrayList<CoverageBean> getCoveragDailyEntry(String visitdate) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setSearch_storeCODE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_CODE)));
                    sb.setSearch_storeNAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    // getCoverageData
    public ArrayList<CoverageBean> getCoverageSpecificData(String store_id, String visit_date) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_ID + "='" + store_id + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setInTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOutTime(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_STATUS)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setImage02(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE02)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    sb.setSearch_storeCODE(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_CODE)));
                    sb.setSearch_storeNAME(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SEARCH_STORE_NAME)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());

        }

        return list;

    }

    //check if table is empty

    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'AND " + CommonString.KEY_COVERAGE_STATUS + "='" + CommonString.KEY_C + "'", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public long updateCoverageStatus(int mid, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_ID + "=" + mid, null);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return l;
    }



    public long updateOutTime(CoverageBean data) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME, data.getOutTime());
            values.put(CommonString.KEY_IMAGE02, data.getImage02());
            values.put(CommonString.KEY_COVERAGE_STATUS, data.getStatus());
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "='" + data.getStoreId() + "' AND "
                    + CommonString.KEY_VISIT_DATE + "='" + data.getVisitDate() + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return l;
    }



    public long updateStoreStatusOnLeave(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);
            l = db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return l;
    }




    public long updateStoreStatusOnCheckout(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_STATUS, status);
            l = db.update("JOURNEY_PLAN", values, CommonString.KEY_STORE_CD + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate
                    + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return l;
    }

    public long updateCoverageStatusOnCheckout(String storeid, String visitdate, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_COVERAGE_STATUS, status);
            l = db.update(CommonString.TABLE_COVERAGE_DATA, values, CommonString.KEY_STORE_ID + "='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate
                    + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return l;
    }

    public ArrayList<AuditQuestion> getStoreAuditHeaderData() {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<AuditQuestion> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT QUESTION_CATEGORY_ID,QUESTION_CATEGORY FROM QUESTIONNAIRE ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditQuestion sb = new AuditQuestion();
                    sb.setQUESTION_CATEGORY_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CATEGORY_ID")));
                    sb.setQUESTION_CATEGORY(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CATEGORY")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            Crashlytics.logException(e);
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public ArrayList<AuditQuestion> getStoreAuditChildData(String ques_category_cd) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<AuditQuestion> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT DISTINCT QUESTION_ID,QUESTION,CAMERA_ALLOW, REMARK_ALLOW FROM QUESTIONNAIRE WHERE QUESTION_CATEGORY_ID='" + ques_category_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditQuestion sb = new AuditQuestion();
                    sb.setQUESTION_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_ID")));
                    sb.setQUESTION(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    sb.setCAMERA_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CAMERA_ALLOW")));
                    sb.setREMARK_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK_ALLOW")));
                    sb.setCurrectanswer_cd("0");
                    sb.setAudit_remark("");
                    sb.setCurrectSPINAnwer("");
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            Crashlytics.logException(e);
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public ArrayList<AuditQuestion> getauditAnswerData(String question_id) {
        ArrayList<AuditQuestion> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT ANSWER_ID,ANSWER FROM QUESTIONNAIRE WHERE QUESTION_ID='" + question_id + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditQuestion df = new AuditQuestion();
                    df.setANSWER_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_ID")));
                    df.setANSWER(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER")));
                    list.add(df);
                    dbcursor.moveToNext();
                }

                dbcursor.close();
                return list;
            }
        } catch (Exception e) {

            return list;
        }

        return list;

    }

    public long insertStoreAuditData(String storeid, String visit_date, HashMap<AuditQuestion,
                                             List<AuditQuestion>> data, List<AuditQuestion> save_listDataHeader) {
        db.delete(CommonString.TABLE_INSERT_AUDIT_OPENINGHEADER_DATA, " STORE_CD='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_AUDIT_DATA, " STORE_CD='" + storeid + "'", null);
        ContentValues values = new ContentValues();
        ContentValues values1 = new ContentValues();
        long l2 = 0;
        try {
            db.beginTransaction();
            for (int i = 0; i < save_listDataHeader.size(); i++) {
                values.put("STORE_CD", storeid);
                values.put(CommonString.KEY_VISIT_DATE, visit_date);
                values.put("QUESTION_CATEGORY_ID", save_listDataHeader.get(i).getQUESTION_CATEGORY_ID().get(0));
                values.put("QUESTION_CATEGORY", save_listDataHeader.get(i).getQUESTION_CATEGORY().get(0));
                long l = db.insert(CommonString.TABLE_INSERT_AUDIT_OPENINGHEADER_DATA, null, values);
                for (int j = 0; j < data.get(save_listDataHeader.get(i)).size(); j++) {
                    values1.put("Common_Id", (int) l);
                    values1.put("STORE_CD", storeid);
                    values1.put(CommonString.KEY_VISIT_DATE, visit_date);
                    values1.put("QUESTION_CATEGORY_ID", save_listDataHeader.get(i).getQUESTION_CATEGORY_ID().get(0));
                    values1.put("QUESTION_CATEGORY", save_listDataHeader.get(i).getQUESTION_CATEGORY().get(0));
                    values1.put("QUESTION", data.get(save_listDataHeader.get(i)).get(j).getQUESTION().get(0));
                    values1.put("QUESTION_CD", data.get(save_listDataHeader.get(i)).get(j).getQUESTION_ID().get(0));
                    values1.put("CAMERA_ALLOW", data.get(save_listDataHeader.get(i)).get(j).getCAMERA_ALLOW().get(0));
                    values1.put("REMARK_ALLOW", data.get(save_listDataHeader.get(i)).get(j).getREMARK_ALLOW().get(0));
                    values1.put("CURRECT_ANSWER", data.get(save_listDataHeader.get(i)).get(j).getCurrectSPINAnwer());
                    values1.put("CURRECT_ANSWER_CD", Integer.parseInt(data.get(save_listDataHeader.get(i)).get(j).getCurrectanswer_cd()));
                    values1.put("AUDIT_IMG", data.get(save_listDataHeader.get(i)).get(j).getAuditQuestion_cam());
                    values1.put("AUDIT_REMARK", data.get(save_listDataHeader.get(i)).get(j).getAudit_remark());
                    l2 = db.insert(CommonString.TABLE_STORE_AUDIT_DATA, null, values1);
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception", " while Insert Posm Master Data " + ex.toString());
        }
        return l2;
    }

    public ArrayList<AuditQuestion> getStoreAuditInsertedData(String store_cd, String questCategory_cd, String visit_date) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<AuditQuestion> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_STORE_AUDIT_DATA + " WHERE STORE_CD ='" + store_cd + "' AND QUESTION_CATEGORY_ID='" + questCategory_cd + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditQuestion sb = new AuditQuestion();
                    sb.setQUESTION(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    sb.setQUESTION_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")));
                    sb.setCAMERA_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CAMERA_ALLOW")));
                    sb.setREMARK_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK_ALLOW")));
                    sb.setCurrectSPINAnwer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CURRECT_ANSWER")));
                    sb.setCurrectanswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CURRECT_ANSWER_CD")));
                    sb.setAuditQuestion_cam(dbcursor.getString(dbcursor.getColumnIndexOrThrow("AUDIT_IMG")));
                    sb.setAudit_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("AUDIT_REMARK")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }

    public boolean isStoreAuditFilled(String storeId, String visit_date) {
        boolean filled = false;

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT QUESTION_CD FROM STORE_AUDIT_DATA WHERE STORE_CD= '" + storeId + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")).equals("")) {
                        filled = false;
                        break;
                    } else {
                        filled = true;
                    }
                    dbcursor.moveToNext();
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }


    public ArrayList<AuditQuestion> getStoreAuditCompleteData(String store_cd) {
        Log.d("Fetching", "Storedata--------------->Start<------------");
        ArrayList<AuditQuestion> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM STORE_AUDIT_DATA WHERE STORE_CD ='" + store_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AuditQuestion sb = new AuditQuestion();
                    sb.setQUESTION(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION")));
                    sb.setQUESTION_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CD")));
                    sb.setCAMERA_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CAMERA_ALLOW")));
                    sb.setREMARK_ALLOW(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REMARK_ALLOW")));
                    sb.setCurrectSPINAnwer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CURRECT_ANSWER")));
                    sb.setCurrectanswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CURRECT_ANSWER_CD")));
                    sb.setAuditQuestion_cam(dbcursor.getString(dbcursor.getColumnIndexOrThrow("AUDIT_IMG")));
                    sb.setAudit_remark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("AUDIT_REMARK")));

                    sb.setQUESTION_CATEGORY_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CATEGORY_ID")));
                    sb.setQUESTION_CATEGORY(dbcursor.getString(dbcursor.getColumnIndexOrThrow("QUESTION_CATEGORY")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception ", "when fetching opening stock!!!!!!!!!!!" + e.toString());
            return list;
        }

        Log.d("Fetching ", "opening stock---------------------->Stop<-----------");
        return list;
    }


    public boolean isJCPDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM JOURNEY_PLAN " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public boolean isNonWorkingReason() {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM NON_WORKING_REASON ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }

        } catch (Exception e) {
            return filled;
        }

        return filled;
    }

    public void insertJCPSELECTEDData(ArrayList<JourneyPlanGetterSetter> data) {
        db.delete("JOURNEY_PLAN", "STORE_CD" + "='"
                + data.get(0).getStore_cd().get(0) + "'AND VISIT_DATE='" + data.get(0).getStore_cd().get(0) + "'", null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.get(i).getStore_cd().get(0)));
                values.put("EMP_CD", Integer.parseInt(data.get(i).getEmp_cd().get(0)));
                values.put("VISIT_DATE", data.get(i).getVISIT_DATE().get(0));
                values.put("KEYACCOUNT", data.get(i).getKey_account().get(0));
                values.put("STORENAME", data.get(i).getStore_name().get(0));
                values.put("CITY", data.get(i).getCity().get(0));
                values.put("UPLOAD_STATUS", data.get(i).getUploadStatus().get(0));
                values.put("CHECKOUT_STATUS", data.get(i).getCheckOutStatus().get(0));
                db.insert("JOURNEY_PLAN", null, values);
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert JCP Data ",
                    ex.toString());
        }

    }

    public ArrayList<JourneyPlanGetterSetter> getJCPTEMPSPECIFICData(String store_cd) {

        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from STORE_LIST where STORE_CD = '" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    sb.setCheckOutStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));
                    sb.setVISIT_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));
                    sb.setStore_type(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORETYPE")));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception!!!!",
                    e.toString());
            return list;
        }

        Log.d("FetchingJCP data--",
                "-------------------");
        return list;

    }

    public ArrayList<JourneyPlanGetterSetter> getJCPTEMPData(String date) {

        Log.d("",
                "------------------");
        ArrayList<JourneyPlanGetterSetter> list = new ArrayList<JourneyPlanGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from STORE_LIST where VISIT_DATE = '" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORE_CD")));
                    sb.setKey_account(dbcursor.getString(dbcursor.getColumnIndexOrThrow("KEYACCOUNT")));
                    sb.setStore_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORENAME")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CITY")));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    sb.setCheckOutStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS")));
                    sb.setVISIT_DATE(dbcursor.getString(dbcursor.getColumnIndexOrThrow("VISIT_DATE")));
                    sb.setEmp_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EMP_CD")));
                    sb.setStore_type(dbcursor.getString(dbcursor.getColumnIndexOrThrow("STORETYPE")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when !!!!!", e.toString());
            return list;
        }

        Log.d("FetchingJCP data------",
                "-------------------");
        return list;

    }

    @SuppressLint("LongLogTag")
    public void insertJCPTEMPData(JourneyPlanGetterSetter data) {
        db.delete("STORE_LIST", null, null);
        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < data.getStore_cd().size(); i++) {
                values.put("STORE_CD", Integer.parseInt(data.getStore_cd().get(i)));
                values.put("EMP_CD", Integer.parseInt(data.getEmp_cd().get(i)));
                values.put("VISIT_DATE", data.getVISIT_DATE().get(i));
                values.put("KEYACCOUNT", data.getKey_account().get(i));
                values.put("STORENAME", data.getStore_name().get(i));
                values.put("CITY", data.getCity().get(i));
                values.put("STORETYPE", data.getStore_type().get(i));
                values.put("UPLOAD_STATUS", data.getUploadStatus().get(i));
                values.put("CHECKOUT_STATUS", data.getCheckOutStatus().get(i));
                db.insert("STORE_LIST", null, values);

            }

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("Database Exception while Insert JCP Data ",
                    ex.toString());
        }
    }

    public void deleteJaurneyPlanData(String store_cd) {
        db.delete("JOURNEY_PLAN", CommonString.KEY_STORE_CD + "='" + store_cd + "'", null);
    }

    public void jcpjourneyPlan(String name) {
        db.execSQL(name);
    }

    public JourneyPlanGetterSetter getStoreStatus(String id) {
        JourneyPlanGetterSetter sb = new JourneyPlanGetterSetter();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from  JOURNEY_PLAN WHERE STORE_CD ='" + id + "'", null);
            if (dbcursor != null) {
                int numrows = dbcursor.getCount();
                dbcursor.moveToFirst();
                for (int i = 0; i < numrows; i++) {
                    sb.setStore_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_CD)));
                    sb.setCheckOutStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKOUT_STATUS"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("UPLOAD_STATUS")));
                    dbcursor.moveToNext();
                }

                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }

        Log.d("FetchingStoredat---------------------->Stop<-----------",
                "-------------------");
        return sb;

    }

    public void deleteOLDJCP(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from JOURNEY_PLAN where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_JCP_DATA, null, null);
                }
                dbcursor.close();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

    }


    public void updateOutTimeVisitorLoginData(String out_time_image, String out_time, String emp_code, String username, String check, String name) {
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_OUT_TIME_IMAGE, out_time_image);
            values.put(CommonString.KEY_OUT_TIME, out_time);

            if (check.equalsIgnoreCase("CPM")) {

                db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                        CommonString.KEY_EMP_CODE + "='" + emp_code + "' and " + CommonString.KEY_USERNAME + " = '" + username + "'", null);
            } else {

                db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                        CommonString.KEY_NAME + "='" + name + "' and " + CommonString.KEY_EXIT + " = '" + check + "'", null);
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public boolean isVistorDataExists(String emp_id, String visitdate) {

        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where EMP_CODE = '" + emp_id + "' and VISIT_DATE = '" + visitdate + "'", null);
            int count = 0;
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    count++;

                    dbcursor.moveToNext();
                }
                dbcursor.close();

                if (count > 0) {
                    return true;
                } else {
                    return false;
                }

            }

        } catch (Exception e) {

            return false;
        }

        return false;

    }

    public ArrayList<VisitorDetailGetterSetter> getVisitorLoginData(String visitdate, String username) {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from TABLE_VISITOR_LOGIN where VISIT_DATE = '" + visitdate + "' and " + CommonString.KEY_USERNAME + " = '" + username + "'"
                    , null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();

                    sb.setKey_id((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("_id"))));
                    sb.setUsername((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_USERNAME))));
                    sb.setName(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    sb.setDesignation(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_DESIGNATION)));
                    sb.setIn_time_img(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IN_TIME_IMAGE)));
                    sb.setOut_time_img(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_OUT_TIME_IMAGE)));
                    sb.setVisit_date(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setIn_time(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IN_TIME)));
                    sb.setOut_time(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)));
                    sb.setUpload_status(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_UPLOADSTATUS)));
                    sb.setIsexit(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_EXIT)));

                    String emp_code = dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CODE));

                    if (emp_code == null) {
                        sb.setEmp_code("0");
                    } else {
                        sb.setEmp_code(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EMP_CODE)));
                    }
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return list;

    }

    public void updateVisitorUploadData(String empid, String name, String check) {

        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_UPLOADSTATUS, "U");

            if (check.equals("CPM")) {
                db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                        CommonString.KEY_EMP_CODE + "='" + empid + "'", null);
            } else {

                db.update(CommonString.TABLE_VISITOR_LOGIN, values,
                        CommonString.KEY_NAME + "='" + name + "' and " + CommonString.KEY_EXIT + " = '" + check + "'", null);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public ArrayList<VisitorDetailGetterSetter> getVisitorData() {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db
                    .rawQuery(
                            "SELECT -1 AS EMP_CD,'Select Visitor' as NAME,'Select' as DESIGNATION union SELECT * FROM VISITOR_DETAIL"
                            , null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();

                    sb.setEmp_code(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("EMP_CD")));
                    sb.setName(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("NAME")));
                    sb.setDesignation(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("DESIGNATION")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public void InsertVisitorLogindata(VisitorDetailGetterSetter visitorLoginGetterSetter) {

        ContentValues values = new ContentValues();

        try {

            values.put(CommonString.KEY_USERNAME, visitorLoginGetterSetter.getUsername());
            values.put(CommonString.KEY_NAME, visitorLoginGetterSetter.getName());
            values.put(CommonString.KEY_DESIGNATION, visitorLoginGetterSetter.getDesignation());
            values.put(CommonString.KEY_IN_TIME_IMAGE, visitorLoginGetterSetter.getIn_time_img());
            values.put(CommonString.KEY_OUT_TIME_IMAGE, visitorLoginGetterSetter.getOut_time_img());
            values.put(CommonString.KEY_EMP_CODE, visitorLoginGetterSetter.getEmp_code());
            values.put(CommonString.KEY_VISIT_DATE, visitorLoginGetterSetter.getVisit_date());
            values.put(CommonString.KEY_IN_TIME, visitorLoginGetterSetter.getIn_time());
            values.put(CommonString.KEY_OUT_TIME, visitorLoginGetterSetter.getOut_time());
            values.put(CommonString.KEY_UPLOADSTATUS, visitorLoginGetterSetter.getUpload_status());
            values.put(CommonString.KEY_EXIT, visitorLoginGetterSetter.getIsexit());

            long key = db.insert(CommonString.TABLE_VISITOR_LOGIN, null, values);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public ArrayList<VisitorDetailGetterSetter> getInsertFeedbackData(String key_id) {
        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_INSERT_FEEDBACK + " WHERE " + CommonString.KEY_COMMON_ID + " = " + key_id, null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();

                    sb.setRating(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK_RATING)));
                    sb.setFeedback(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK)));
                    sb.setFeedback_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK_CD)));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REMARK)));
                    sb.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COMMON_ID)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {

            return list;
        }

        Log.d("Fetching non working data---------------------->Stop<-----------",
                "-------------------");
        return list;
    }

    public ArrayList<VisitorDetailGetterSetter> getInsertFeedbackUploadData(String key_id) {
        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_INSERT_FEEDBACK + " WHERE " + CommonString.KEY_COMMON_ID + " = " + key_id, null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();

                    sb.setRating(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK_RATING)));
                    sb.setFeedback(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK)));
                    sb.setFeedback_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_FEEDBACK_CD)));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REMARK)));
                    sb.setName(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_NAME)));
                    sb.setDesignation(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_DESIGNATION)));
                    sb.setKey_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COMMON_ID)));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {

            return list;
        }

        return list;
    }


    public long InsertfeedbackData(FeedbackGetterSetter data) {
        // db.delete(CommonString.TABLE_feedback_save, CommonString.COMMON_ID + "='" +key_id  + "'", null);
        ContentValues values = new ContentValues();
        long id = 0;

        try {

            values.put(CommonString.KEY_VISITOR_NAME, data.getVisitor_name());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisit_date());
            values.put(CommonString.KEY_VISITOR_DESIGNATION, data.getVisitor_designation());
            values.put(CommonString.KEY_STATUS, data.getStatus());
            values.put(CommonString.KEY_USER_ID, data.getUser_id());

            id = db.insert(CommonString.TABLE_feedback_save, null, values);
            return id;
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return -1;
        }
    }
    public long InsertFeedbackData(ArrayList<VisitorDetailGetterSetter> data, String exit, String date, String remark, String name, String designation, String key_id) {

        db.delete(CommonString.TABLE_INSERT_FEEDBACK, CommonString.KEY_NAME + "='" + name + "' and " + CommonString.KEY_COMMON_ID + " = '" + key_id + "'", null);

        ContentValues values = new ContentValues();
        long id = 0;
        try {
            for (int i = 0; i < data.size(); i++) {
                values.put(CommonString.KEY_COMMON_ID, key_id);
                values.put(CommonString.KEY_FEEDBACK_CD, data.get(i).getFeedback_cd());
                values.put(CommonString.KEY_FEEDBACK, data.get(i).getFeedback());
                values.put(CommonString.KEY_EXIT, exit);
                values.put(CommonString.KEY_VISIT_DATE, date);
                values.put(CommonString.KEY_FEEDBACK_RATING, data.get(i).getRating());
                values.put(CommonString.KEY_REMARK, remark);
                values.put(CommonString.KEY_NAME, name);
                values.put(CommonString.KEY_DESIGNATION, designation);

                id = db.insert(CommonString.TABLE_INSERT_FEEDBACK, null, values);
            }

            return id;
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return -1;
        }

    }

    public ArrayList<VisitorDetailGetterSetter> getFeedaback_question() {

        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<VisitorDetailGetterSetter>();
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * from FEEDBACK_QUESTION", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter pgs = new VisitorDetailGetterSetter();

                    pgs.setFeedback_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FQUESTION_ID")));
                    pgs.setFeedback(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FQUESTION")));
                    list.add(pgs);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }

        return list;

    }

    public ArrayList<VisitorDetailGetterSetter> getRatingData() {
        Log.d("FetchingAssetdata--------------->Start<------------",
                "------------------");
        ArrayList<VisitorDetailGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM FEEDBACK_RATING", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    VisitorDetailGetterSetter sb = new VisitorDetailGetterSetter();
                    sb.setRating(dbcursor.getString(dbcursor.getColumnIndexOrThrow("RATING")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }



}
