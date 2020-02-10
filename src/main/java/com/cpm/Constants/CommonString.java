package com.cpm.Constants;

import android.os.Environment;

public class CommonString {
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/Godrejsupervisor/";
    public static final String BACKUP_FILE_PATH = Environment.getExternalStorageDirectory() + "/GODREJ_SUP_backup/";
    public static final String DATA_DELETE_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final int CAPTURE_MEDIA = 131;
    // preferenec keys
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember";
    public static final String KEY_RIGHT_NAME = "right_name";
    public static final String KEY_PATH = "path";
    public static final String KEY_VERSION = "version";
    public static final String METHOD_UPLOAD_XML = "DrUploadXml";
    public static final String MEHTOD_UPLOAD_COVERAGE_STATUS = "UploadCoverage_Status";
    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final String KEY_USER_TYPE = "RIGHTNAME";
    public static final String KEY_DATE = "date";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "Y";
    public static final String KEY_CHECK_IN = "I";
    public static final String KEY_INVALID = "INVALID";
    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_VALID = "Valid";
    public static final String SOAP_ACTION = "http://tempuri.org/";
    public static final String KEY_MERCHANDISER_ID = "MERCHANDISER_ID";
    public static final String ERROR = " PROBLEM OCCURED IN ";
    public static final String KEY_SUCCESS_chkout = "Success";
    public static final String TABLE_JCP_DATA = "JOURNEY_PLAN";

    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FALSE = "False";
    public static final String KEY_CHANGED = "Changed";
    public static final String KEY_NO_DATA = "NoData";
    public static final String KEY_IMAGE = "IMAGE1";
    public static final String KEY_IMAGE02 = "IMAGE2";
    public static final String KEY_COVERAGE_REMARK = "REMARK";
    public static final String MESSAGE_UPLOAD_DATA = "Data Uploaded Successfully";

    ///for importent
    public static final String METHOD_UPLOAD_IMAGE = "GetImageWithFolderName";
    public static final String SOAP_ACTION_UPLOAD_IMAGE = "http://tempuri.org/" + METHOD_UPLOAD_IMAGE;
    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String URL = "http://gs.parinaam.in/gswebservice.asmx";

    public static final String METHOD_SEARCH_LIST_STORE = "Storeserach";
    public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/"
            + METHOD_LOGIN;

    public static final String METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING = "DowloadPromotionWithComplainceByMapping";
    public static final String SOAP_ACTION_DOWNLAOD_COMPLIANCE_MAPPING = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING;

    public static final String METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING_SPECIAL = "DownLoad_PROMOTION_COMPLIANCE_MAPPING_SPECIAL";
    public static final String SOAP_ACTION_DOWNLAOD_COMPLIANCE_MAPPING_SPECIAL = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING_SPECIAL;

    public static final String METHOD_VERTICAL_MASTER = "DOWLOAD_VERTICALMASTER";
    public static final String SOAP_ACTION_VERTICAL_MASTER = "http://tempuri.org/"
            + METHOD_VERTICAL_MASTER;

    public static final String METHOD_BRAND_MASTER = "DOWLOAD_BRANDMASTER";
    public static final String SOAP_ACTION_BRAND_Master = "http://tempuri.org/"
            + METHOD_BRAND_MASTER;

    public static final String METHOD_VERTICAL_BRAND_MAPPING = "DOWLOAD_VERTICALBRANDMAPPING";
    public static final String SOAP_ACTION_VERTICAL_BRAND_Mapping = "http://tempuri.org/"
            + METHOD_VERTICAL_BRAND_MAPPING;

    public static final String METHOD_VERTICAL_SKU_MAPPING = "SKUBRANDMAPPINGDownload";
    public static final String SOAP_ACTION_VERTICAL_SKU_Mapping = "http://tempuri.org/"
            + METHOD_VERTICAL_SKU_MAPPING;

    public static final String METHOD_CATEGORY_MASTER = "DOWLOAD_CATEGORYMASTER";
    public static final String SOAP_ACTION_CATEGORY_MASTER = "http://tempuri.org/"
            + METHOD_CATEGORY_MASTER;

    public static final String METHOD_CATEGORY_SKU_MAPPING = "CATEGORYSKUMAPPINGDownload";
    public static final String SOAP_ACTION_CATEGORY_SKU_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_SKU_MAPPING;

    public static final String METHOD_CATEGORY_VERTICAL_MAPPING = "CATEGORYVERTICALMAPPINGDownload";
    public static final String SOAP_ACTION_CATEGORY_VERTICAL_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_VERTICAL_MAPPING;

    public static final String METHOD_CATEGORY_POSM_MAPPING = "POSMBRANDMAPPINGDownload";
    public static final String SOAP_ACTION_POSM_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_POSM_MAPPING;

    public static final String METHOD_SKU_MASTER_DOWNLOAD = "SKU_MASTERDownload";
    public static final String SOAP_ACTION_SKU_MASTER = "http://tempuri.org/"
            + METHOD_SKU_MASTER_DOWNLOAD;

    public static final String METHOD_COMPANY_MASTER_DOWNLOAD = "COMPANY_MASTERDownload";
    public static final String SOAP_ACTION_COMPANY_MASTER = "http://tempuri.org/"
            + METHOD_COMPANY_MASTER_DOWNLOAD;

    // Shahab
    public static final String METHOD_NONSKU_REASON = "DOWLOAD_NON_STOCK_REASON_MASTER";
    public static final String SOAP_ACTION_NONSKU_REASON = "http://tempuri.org/"
            + METHOD_NONSKU_REASON;

    public static final String METHOD_SKU_FOCUS_DOWNLOAD = "SKUAVALIBILITY_FOCUS";
    public static final String SOAP_ACTION_SKU_FOCUS = "http://tempuri.org/"
            + METHOD_SKU_FOCUS_DOWNLOAD;

    public static final String METHOD_MAPPING_COMPETITOR = "DOWLOAD_MAPPINGCOMPEPITORBRAND";
    public static final String SOAP_ACTION_MAPPING_COMPETITOR = "http://tempuri.org/"
            + METHOD_MAPPING_COMPETITOR;

    public static final String METHOD_POSM_MASTER_DOWNLOAD = "DOWLOAD_POSMMASTER";
    public static final String SOAP_ACTION_POSM_MASTER_DOWNLOAD = "http://tempuri.org/"
            + METHOD_POSM_MASTER_DOWNLOAD;

    // Upload Coverage
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE = "UPLOAD_COVERAGE1";
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE_LOC = "UPLOAD_COVERAGE_SUP";

    public static final String SOAP_ACTION_UPLOAD_DR_STORE_COVERAGE = "http://tempuri.org/"
            + METHOD_UPLOAD_DR_STORE_COVERAGE_LOC;

    public static final String METHOD_GENERIC_UPLOAD = "DrUploadXml";
    public static final String METHOD_UPLOAD_DR_STORE_DATA = "Upload_Store_Layout_V1";
    public static final String SOAP_ACTION_UPLOAD_DR_STORE_DATA = "http://tempuri.org/"
            + METHOD_UPLOAD_DR_STORE_DATA;

    public static final String SOAP_ACTION_METHOD_GENERIC_UPLOAD = "http://tempuri.org/"
            + METHOD_GENERIC_UPLOAD;

    public static final String METHOD_UPLOAD_DR_RETAILER_INFO = "Upload_DR_STORE_PAYMENT";
    public static final String SOAP_ACTION_UPLOAD_DR_RETAILER_INFO = "http://tempuri.org/"
            + METHOD_UPLOAD_DR_RETAILER_INFO;

    public static final String METHOD_UPLOAD_ASSET = "Upload_Stock_Availiablity_V1";
    public static final String SOAP_ACTION_UPLOAD_ASSET = "http://tempuri.org/"
            + METHOD_UPLOAD_ASSET;

    public static final String METHOD_UPLOAD_SEC_SKU = "Upload_Stock_Availiablity_SECONDARY";
    public static final String SOAP_ACTION_UPLOAD_SEC_SKU = "http://tempuri.org/"
            + METHOD_UPLOAD_SEC_SKU;

    public static final String METHOD_UPLOAD_PCKGE_SKU = "Upload_DR_CORE_SKU_PACKAGING";
    public static final String SOAP_ACTION_UPLOAD_PCKGE_SKU = "http://tempuri.org/"
            + METHOD_UPLOAD_PCKGE_SKU;

    public static final String METHOD_UPLOAD_POSM = "Upload_Posm_Deployed";
    public static final String SOAP_ACTION_UPLOAD_POSM = "http://tempuri.org/"
            + METHOD_UPLOAD_POSM;

    public static final String METHOD_UPLOAD_COMPLIANCE = "Upload_Promotion_WindowExists";
    public static final String SOAP_ACTION_COMPLIANCE = "http://tempuri.org/"
            + METHOD_UPLOAD_COMPLIANCE;

    public static final String METHOD_UPLOAD_COMPLIANCE_SPECIAL = "Upload_Promotion_Special";
    public static final String SOAP_ACTION_COMPLIANCE_SPECIAL = "http://tempuri.org/"
            + METHOD_UPLOAD_COMPLIANCE_SPECIAL;

    public static final String METHOD_NON_WORKING_MASTER = "DOWLOAD_NONWORKINGREGIONMASTER";
    public static final String SOAP_ACTION_NONWORKING = "http://tempuri.org/"
            + METHOD_NON_WORKING_MASTER;

    public static final String METHOD_SET_COVERAGE_STATUS = "Upload_Status";
    public static final String SOAP_ACTION_SET_COVERAGE_STATUS = "http://tempuri.org/"
            + METHOD_SET_COVERAGE_STATUS;

    public static final String METHOD_SET_UPLOAD_GEODATA = "Upload_Store_Geo_Tag";
    public static final String SOAP_ACTION_UPLOAD_GEODATA = "http://tempuri.org/"
            + METHOD_SET_UPLOAD_GEODATA;

    // database

    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";

    public static final String KEY_ID = "_id";
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_COVERAGE_STATUS = "Coverage";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_CHECKOUT_STATUS = "CHECKOUT_STATUS";

    public static final String KEY_STORE_CD = "STORE_CD";

    public static final String KEY_EMP_CD = "EMP_CD";
    public static final String KEY_SEARCH_STORE_CODE = "S_STORE_CODE";
    public static final String KEY_SEARCH_STORE_NAME = "S_STORE_NAME";
    public static final String KEY_STOREVISITED = "STORE_VISITED";
    public static final String KEY_STOREVISITED_STATUS = "STORE_VISITED_STATUS";

    public static final String KEY_FOOD_STORE = "FOOD_STORE";

    // POSM Master
    public static final String METHOD_NAME_DOWNLOAD_POSM_MASTER = "POSM";
    public static final String SOAP_ACTION_DOWNLAOD_POSM_MASTER = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_POSM_MASTER;
    // geo tag
    public static final String METHOD_NAME_DOWNLOAD_GEO = "DownLoadStoreByUser";
    public static final String SOAP_ACTION_DOWNLAOD_GEO = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_GEO;

    public static final String SOAP_ACTION_COVERAGE_UPLOAD = "http://tempuri.org/"
            + METHOD_UPLOAD_DR_STORE_COVERAGE;


    public static final String METHOD_Get_DR_STORE_IMAGES = "GET_StoreLayout_IMAGES";
    public static final String SOAP_ACTION_Get_DR_STORE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_STORE_IMAGES;

    public static final String METHOD_Get_DR_CHEQUE_IMAGES = "Upload_StoreCheque_IMAGES";
    public static final String SOAP_ACTION_Get_DR_CHEQUE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_CHEQUE_IMAGES;

    public static final String METHOD_Get_DR_POSM_IMAGES = "GET_PosmDepLoyed_IMAGES";
    public static final String SOAP_ACTION_Get_DR_POSM_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_POSM_IMAGES;

    public static final String METHOD_Get_DR_COMPLIANCE_IMAGES = "GET_Store_SecondaryWindowImage";
    public static final String SOAP_ACTION_Get_DR_COMPLIANCE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_COMPLIANCE_IMAGES;

    public static final String METHOD_Get_DR_STORE_IMAGES_GEO = "Upload_StoreGeoTag_IMAGES";
    public static final String SOAP_ACTION_DR_STORE_IMAGES_GEO = "http://tempuri.org/"
            + METHOD_Get_DR_STORE_IMAGES_GEO;


    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";
    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/"
            + METHOD_NAME_UNIVERSAL_DOWNLOAD;
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_INVALID_XML = "Problem Occured while parsing XML : invalid data";

    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Please Check Your Network Connection";

    public static final String TABLE_VISITOR_LOGIN = "TABLE_VISITOR_LOGIN";
    public static final String KEY_EMP_CODE = "EMP_CODE";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_DESIGNATION = "DESIGNATION";
    public static final String KEY_FEEDBACK_IMAGE = "FEEDBACK_IMAGE";
    public static final String KEY_UPLOADSTATUS = "UPLOADSTATUS";
    public static final String KEY_IN_TIME_IMAGE = "IN_TIME_IMAGE";
    public static final String KEY_OUT_TIME_IMAGE = "OUT_TIME_IMAGE";
    public static final String KEY_EXIT = "EXIT";
    public static final String KEY_FEEDBACK = "FEEDBACK";
    public static final String KEY_FEEDBACK_CD = "FEEDBACK_CD";
    public static final String KEY_REMARK = "REMARK";
    public static final String KEY_COMMON_ID = "COMMON_ID";
    public static final String KEY_FQUESTION_ID = "FQUESTION_ID";
    public static final String KEY_FQUESTION = "FQUESTION";
    public static final String KEY_FEEDBACK_RATING = "FEEDBACK_RATING";
    public static final String KEY_VISITOR_NAME = "VISITOR_NAME";
    public static final String KEY_VISITOR_DESIGNATION = "VISITOR_DESIGNATION";

    public static final int TIMEOUT = 20000;
    public static final String CREATE_TABLE_VISITOR_LOGIN = "CREATE TABLE "
            + TABLE_VISITOR_LOGIN + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_USERNAME + " VARCHAR,"
            + KEY_EMP_CODE + " VARCHAR,"
            + KEY_NAME + " VARCHAR,"
            + KEY_DESIGNATION + " VARCHAR,"
            + KEY_UPLOADSTATUS + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_IN_TIME_IMAGE + " VARCHAR,"
            + KEY_EXIT + " VARCHAR,"
            + KEY_OUT_TIME_IMAGE + " VARCHAR)";


    public static final String TABLE_INSERT_FEEDBACK = "FEEDBACK";
    public static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_FEEDBACK
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_COMMON_ID + " INTEGER,"
            + KEY_FEEDBACK_CD + " INTEGER,"
            + KEY_FEEDBACK + " VARCHAR,"
            + KEY_EXIT + " VARCHAR,"
            + KEY_REMARK + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_NAME + " VARCHAR,"
            + KEY_DESIGNATION + " VARCHAR,"
            + KEY_FEEDBACK_RATING + " INTEGER"
            + ")";

    public static final String TABLE_feedback_save = "FEEDBACK_save";
    public static final String CREATE_feedback_save = "CREATE TABLE IF NOT EXISTS "
            + TABLE_feedback_save
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_VISITOR_NAME + " VARCHAR,"
            + KEY_VISITOR_DESIGNATION + " VARCHAR,"
            + KEY_STATUS + " VARCHAR,"
            + KEY_USER_ID + " VARCHAR"
            + ")";


    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_COVERAGE_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR,USER_ID VARCHAR, " + KEY_IN_TIME + " VARCHAR,"
            + KEY_OUT_TIME + " VARCHAR,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE + " VARCHAR," + KEY_MERCHANDISER_ID + " VARCHAR,"
            + KEY_COVERAGE_STATUS + " VARCHAR," + KEY_IMAGE + " VARCHAR,"
            + KEY_SEARCH_STORE_CODE + " VARCHAR," + KEY_SEARCH_STORE_NAME + " VARCHAR,"
            + KEY_IMAGE02 + " VARCHAR,"

            + KEY_REASON_ID + " VARCHAR," + KEY_COVERAGE_REMARK
            + " VARCHAR," + KEY_REASON + " VARCHAR)";


    public static final String TABLE_INSERT_AUDIT_OPENINGHEADER_DATA = "AUDIT_OPENINGHEADER_DATA";
    public static final String CREATE_TABLE_AUDIT_OPENINGHEADER_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_INSERT_AUDIT_OPENINGHEADER_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_CD
            + " INTEGER, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + "QUESTION_CATEGORY_ID" + " INTEGER,"
            + " QUESTION_CATEGORY" + " VARCHAR)";


    public static final String TABLE_STORE_AUDIT_DATA = "STORE_AUDIT_DATA";

    public static final String CREATE_TABLE_STORE_AUDIT_DATA = "CREATE TABLE  IF NOT EXISTS "
            + TABLE_STORE_AUDIT_DATA + " (" + "Common_Id"
            + " INTEGER  ," + KEY_STORE_CD
            + " INTEGER, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + "QUESTION_CATEGORY_ID" + " INTEGER, "
            + "QUESTION" + " VARCHAR, "
            + "QUESTION_CD" + " INTEGER, "
            + "CURRECT_ANSWER" + " VARCHAR, "
            + "REMARK_ALLOW" + " INTEGER, "
            + "CAMERA_ALLOW" + " INTEGER, "
            + "CURRECT_ANSWER_CD" + " INTEGER, "
            + "AUDIT_IMG" + " VARCHAR, "
            + "AUDIT_REMARK" + " VARCHAR, "
            + " QUESTION_CATEGORY" + " VARCHAR)";

}
