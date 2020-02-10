package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 3/22/2018.
 */

public class AuditQuestion {
    public String AuditQuestionTable;
    ArrayList<String>QUESTION_CATEGORY_ID=new ArrayList<>();
    ArrayList<String>QUESTION_CATEGORY=new ArrayList<>();
    ArrayList<String>QUESTION_ID=new ArrayList<>();
    ArrayList<String>QUESTION=new ArrayList<>();
    ArrayList<String>ANSWER_ID=new ArrayList<>();
    ArrayList<String>ANSWER=new ArrayList<>();
    String currectanswer_cd="";

    public String getAuditQuestion_cam() {
        return auditQuestion_cam;
    }

    public void setAuditQuestion_cam(String auditQuestion_cam) {
        this.auditQuestion_cam = auditQuestion_cam;
    }

    String auditQuestion_cam="";

    public String getCurrectanswer_cd() {
        return currectanswer_cd;
    }
    String currectSPINAnwer="";

    public void setQUESTION_CATEGORY_ID(ArrayList<String> QUESTION_CATEGORY_ID) {
        this.QUESTION_CATEGORY_ID = QUESTION_CATEGORY_ID;
    }

    public String getCurrectSPINAnwer() {
        return currectSPINAnwer;
    }

    public void setCurrectSPINAnwer(String currectSPINAnwer) {
        this.currectSPINAnwer = currectSPINAnwer;
    }

    public void setCurrectanswer_cd(String currectanswer_cd) {
        this.currectanswer_cd = currectanswer_cd;
    }

    public String getAudit_remark() {
        return audit_remark;
    }

    public void setAudit_remark(String audit_remark) {
        this.audit_remark = audit_remark;
    }

    String audit_remark="";

    public String getAuditQuestionTable() {
        return AuditQuestionTable;
    }

    public void setAuditQuestionTable(String auditQuestionTable) {
        AuditQuestionTable = auditQuestionTable;
    }

    public ArrayList<String> getQUESTION_CATEGORY_ID() {
        return QUESTION_CATEGORY_ID;
    }

    public void setQUESTION_CATEGORY_ID(String QUESTION_CATEGORY_ID) {
        this.QUESTION_CATEGORY_ID.add(QUESTION_CATEGORY_ID);
    }

    public ArrayList<String> getQUESTION_CATEGORY() {
        return QUESTION_CATEGORY;
    }

    public void setQUESTION_CATEGORY(String QUESTION_CATEGORY) {
        this.QUESTION_CATEGORY.add(QUESTION_CATEGORY);
    }

    public ArrayList<String> getQUESTION_ID() {
        return QUESTION_ID;
    }

    public void setQUESTION_ID(String QUESTION_ID) {
        this.QUESTION_ID.add(QUESTION_ID);
    }

    public ArrayList<String> getQUESTION() {
        return QUESTION;
    }

    public void setQUESTION(String QUESTION) {
        this.QUESTION.add(QUESTION);
    }

    public ArrayList<String> getANSWER_ID() {
        return ANSWER_ID;
    }

    public void setANSWER_ID(String ANSWER_ID) {
        this.ANSWER_ID.add(ANSWER_ID);
    }

    public ArrayList<String> getANSWER() {
        return ANSWER;
    }

    public void setANSWER(String ANSWER) {
        this.ANSWER.add(ANSWER);
    }

    public ArrayList<String> getCAMERA_ALLOW() {
        return CAMERA_ALLOW;
    }

    public void setCAMERA_ALLOW(String CAMERA_ALLOW) {
        this.CAMERA_ALLOW.add(CAMERA_ALLOW);
    }

    public ArrayList<String> getREMARK_ALLOW() {
        return REMARK_ALLOW;
    }

    public void setREMARK_ALLOW(String REMARK_ALLOW) {
        this.REMARK_ALLOW.add(REMARK_ALLOW);
    }

    ArrayList<String>CAMERA_ALLOW=new ArrayList<>();
    ArrayList<String>REMARK_ALLOW=new ArrayList<>();
}
